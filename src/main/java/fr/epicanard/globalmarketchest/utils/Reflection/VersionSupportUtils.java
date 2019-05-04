package fr.epicanard.globalmarketchest.utils.Reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.utils.Annotations.MethodName;
import fr.epicanard.globalmarketchest.utils.Annotations.Version;

public class VersionSupportUtils {

  private final String NBTTAG = "GMCItem";

  enum Path {
    BUKKIT("org.bukkit.craftbukkit"),
    MINECRAFT("net.minecraft.server");

    String path;

    Path(String path) {
      this.path = path;
    }
  }

  private String version;
  private static VersionSupportUtils INSTANCE;

  /**
   * Private constructor VersionSupportUtils
   */
  private VersionSupportUtils() {
    this.version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
  }

  /**
   * Singleton, method to get the instance of this class
   */
  public static VersionSupportUtils getInstance() {
    if (INSTANCE == null)
      INSTANCE = new VersionSupportUtils();
    return INSTANCE;
  }


  // ======== TOOLS ============

  /**
   * Get a class from his path
   *
   * @param basePath Base of the pathe to define if it is a bukkit ou minecraft
   * @param path Name of the classe
   * @return return the class with the path
   * @throws ClassNotFoundException
   */
  private Class<?> getClassFromPath(Path basePath, String path) throws ClassNotFoundException {
    return Class.forName(String.format("%s.%s.%s", basePath.path, this.version, path));
  }

  /**
   * Transform an object array into a class array
   *
   * @param args Object array
   * @return return an array of class from each object
   */
  private Class<?>[] fromObjectToClass(Object[] args) {
    Class<?>[] classes = new Class<?>[0];

    if (args != null) {
      classes = new Class<?>[args.length];
      for (int i = 0; i < args.length; i++) {
        classes[i] = args[i].getClass();
      }
    }
    return classes;
  }

  /**
   * Invoke the method of an object
   *
   * @param object the object on which call the method
   * @param method the method name to calle
   * @param args all the arguments that must be send to the method
   * @return return the object return by the method
   */
  private Object invokeMethod(Object object, String method, Object ...args) {
    try {
      return object.getClass().getMethod(method, fromObjectToClass(args)).invoke(object, args);
    } catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Object newInstance(String path, Object ...args) {
    try {
      return getClassFromPath(Path.MINECRAFT, path).getConstructor(fromObjectToClass(args)).newInstance(args);
    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }


  /**
   * Get the static object Item.REGISTRY
   *
   * @return return the static object
   */
  private Object getRegistry() {
    try {
      try {
        Object registry = getClassFromPath(Path.MINECRAFT, "Item").getField("REGISTRY").get(null);
        return registry;
      } catch (NoSuchFieldException e) {
        return getClassFromPath(Path.MINECRAFT, "IRegistry").getField("ITEM").get(null);
      }
    } catch(ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {}
    return null;
  }


  // ======= SPECIFIC METHOD ===========

  /**
   * Get an itemStack with is minecraft key
   *
   * @param name
   * @return
   */
  public ItemStack getItemStack(String name) {
    try {
      Class<?> minecraftKeyClass = getClassFromPath(Path.MINECRAFT, "MinecraftKey");
      Object minecraftKey = minecraftKeyClass.getConstructor(String.class).newInstance(name);

      Object registry = getRegistry();
      Object item = registry.getClass().getMethod("get", minecraftKeyClass).invoke(registry, minecraftKey);
      if (item == null)
        return null;

      Method asNewCraftStack = getClassFromPath(Path.BUKKIT, "inventory.CraftItemStack").getMethod("asNewCraftStack", getClassFromPath(Path.MINECRAFT, "Item"));
      ItemStack itemStack = (ItemStack)asNewCraftStack.invoke(null, item);

      return this.setNbtTag(itemStack);

    } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
    }

    return null;
  }


  /**
   * Get the minecraftkey in string. Format minecraft:dirt
   *
   * @param item
   *          ItemStack
   * @return return the string minecraft key
   */
  public String getMinecraftKey(ItemStack itemStack) {
    try {
      Method asNMSCopy = getClassFromPath(Path.BUKKIT, "inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class);
      Object nmsItemStack = asNMSCopy.invoke(null, itemStack);

      Object registry = getRegistry();
      Object minecraftKey;
      try {
        minecraftKey = registry.getClass().getMethod("b", Object.class).invoke(registry, invokeMethod(nmsItemStack, "getItem"));
      } catch(NoSuchMethodException e) {
        minecraftKey = registry.getClass().getMethod("getKey", Object.class).invoke(registry, invokeMethod(nmsItemStack, "getItem"));
      }

      return invokeMethod(minecraftKey, "b").toString() + ":" + invokeMethod(minecraftKey, "getKey").toString();

    } catch(ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get the display name of an itemstack
   *
   * @param item ItemStack
   * @return return the displayname
   */
  public String getItemStackDisplayName(ItemStack itemStack) {
    try {
      Method asNMSCopy = getClassFromPath(Path.BUKKIT, "inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class);
      Object nmsItemStack = asNMSCopy.invoke(null, itemStack);

      return this.invokeMethod(this.invokeMethod(nmsItemStack, "getName"), "getString").toString();
    } catch(ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Update the inventory name update by the player
   *
   * @param title The new inventory Name
   * @param player The player
   */
  @MethodName("updateInventoryName")
  @Version("1.13")
  public void updateInventoryName_1_13(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = newInstance("ChatMessage", title, new Object[]{});
      VersionField activeContainerVF = VersionField.from(entityPlayer).get("activeContainer");
      Object windowId = activeContainerVF.get("windowId").value();

      Class<?> iChat = getClassFromPath(Path.MINECRAFT, "IChatBaseComponent");

      Object packet = getClassFromPath(Path.MINECRAFT, "PacketPlayOutOpenWindow").getConstructor(Integer.TYPE, String.class, iChat, Integer.TYPE)
        .newInstance( windowId, "minecraft:chest", iChat.cast(chatMessage), player.getOpenInventory().getTopInventory().getSize());


      Object playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);

      playerConnection.getClass().getMethod("sendPacket", getClassFromPath(Path.MINECRAFT, "Packet")).invoke(playerConnection, packet);
      entityPlayer.getClass().getMethod("updateInventory", getClassFromPath(Path.MINECRAFT, "Container")).invoke(entityPlayer, activeContainerVF.value());

    } catch(NoSuchFieldException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException  e) {
      e.printStackTrace();
    }
  }

  /**
   * Update the inventory name update by the player
   *
   * @param title The new inventory Name
   * @param player The player
   */
  @MethodName("updateInventoryName")
  @Version
  public void updateInventoryName_latest(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = newInstance("ChatMessage", title, new Object[]{});
      VersionField activeContainerVF = VersionField.from(entityPlayer).get("activeContainer");
      Object windowId = activeContainerVF.get("windowId").value();

      Class<?> iChat = getClassFromPath(Path.MINECRAFT, "IChatBaseComponent");
      Class<?> containers = getClassFromPath(Path.MINECRAFT, "Containers");

      Object packet = getClassFromPath(Path.MINECRAFT, "PacketPlayOutOpenWindow").getConstructor(Integer.TYPE, containers, iChat)
        .newInstance( windowId, containers.getField("GENERIC_9X6").get(null), iChat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);

      playerConnection.getClass().getMethod("sendPacket", getClassFromPath(Path.MINECRAFT, "Packet")).invoke(playerConnection, packet);
      entityPlayer.getClass().getMethod("updateInventory", getClassFromPath(Path.MINECRAFT, "Container")).invoke(entityPlayer, activeContainerVF.value());

    } catch(NoSuchFieldException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException  e) {
      e.printStackTrace();
    }
  }

  /**
   * Define if the GMC NBT TAG is set on this item
   * 
   * @param itemStack Item to analyze
   * @return Return if the item as gmc nbt tag
   */
  public boolean hasNbtTag(ItemStack itemStack) {
    try {
      Method asNMSCopy = getClassFromPath(Path.BUKKIT, "inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class);
      Object nmsItemStack = asNMSCopy.invoke(null, itemStack);

      Object tagCompound = this.invokeMethod(nmsItemStack, "getTag");

      return (tagCompound != null && (Boolean)this.invokeMethod(tagCompound, "hasKey", this.NBTTAG));

    } catch(ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Set the custom GMC NBT TAG on item in parameter
   * 
   * @param itemStack Item on which add NBT TAG
   * @return ItemStack modified
   */
  public ItemStack setNbtTag(ItemStack itemStack) {
    if (itemStack == null) { return null; }
    if (this.hasNbtTag(itemStack)) { return itemStack; }

    try {
      Class<?> craftItemStack = getClassFromPath(Path.BUKKIT, "inventory.CraftItemStack");
      Method asNMSCopy = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
      Object nmsItemStack = asNMSCopy.invoke(null, itemStack);

      Object tagCompound = this.invokeMethod(nmsItemStack, "getTag");

      if (tagCompound == null) {
        tagCompound = this.newInstance("NBTTagCompound");
      }

      tagCompound.getClass().getMethod("setBoolean", String.class, boolean.class).invoke(tagCompound, this.NBTTAG, true);

      this.invokeMethod(nmsItemStack, "setTag", tagCompound);
      return (ItemStack)craftItemStack.getDeclaredMethod("asBukkitCopy", nmsItemStack.getClass()).invoke(null, nmsItemStack);

    } catch(ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return itemStack;
  }

}