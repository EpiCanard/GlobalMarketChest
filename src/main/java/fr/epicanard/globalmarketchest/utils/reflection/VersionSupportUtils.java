package fr.epicanard.globalmarketchest.utils.reflection;

import fr.epicanard.globalmarketchest.utils.Utils;
import fr.epicanard.globalmarketchest.utils.annotations.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Optional;

import static fr.epicanard.globalmarketchest.utils.annotations.AnnotationCaller.call;
import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.*;

public class VersionSupportUtils {

  private final String NBTTAG = "GMCItem";

  enum Path {
    BUKKIT("org.bukkit.craftbukkit"),
    MINECRAFT_SERVER("net.minecraft.server"),
    MINECRAFT_RESOURCES("net.minecraft.resources"),
    MINECRAFT_WORLD_ITEM("net.minecraft.world.item"),
    MINECRAFT_NETWORK_CHAT("net.minecraft.network.chat"),
    MINECRAFT_NETWORK_GAME("net.minecraft.network.protocol.game"),
    MINECRAFT_NETWORK_PROTOCOL("net.minecraft.network.protocol"),
    MINECRAFT_WORLD_INVENTORY("net.minecraft.world.inventory"),
    MINECRAFT_NBT("net.minecraft.nbt"),
    MINECRAFT_CORE_REGISTRIES("net.minecraft.core.registries"),
    MINECRAFT_CORE("net.minecraft.core");

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

  private Object newInstance(Class<?> clazz, Object... args) {
    try {
      return clazz.getConstructor(fromObjectToClass(args)).newInstance(args);
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  // ======== TOOLS ============

  /**
   * Get a class from his path
   *
   * @param basePath Base of the pathe to define if it is a bukkit ou minecraft
   * @param path     Name of the classe
   * @return return the class with the path
   * @throws ClassNotFoundException
   */
  private Class<?> getClassFromPathWithVersion(Path basePath, String path) throws ClassNotFoundException {
    return Class.forName(String.format("%s.%s.%s", basePath.path, this.version, path));
  }

  /**
   * Get a class from his path
   *
   * @param basePath Base of the pathe to define if it is a bukkit ou minecraft
   * @param path     Name of the classe
   * @return return the class with the path
   * @throws ClassNotFoundException
   */
  private Class<?> getClassFromPath(Path basePath, String path) throws ClassNotFoundException {
    return Class.forName(String.format("%s.%s", basePath.path, path));
  }

  /**
   * Get the static object Item.REGISTRY
   *
   * @return return the static object
   */
  private Object getRegistry() {
    try {
      return call("getRegistry", this);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Version(name = "getRegistry", versions = { "1.12" })
  public Object getRegistry_1_12() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
    return getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "Item").getField("REGISTRY").get(null);
  }

  @Version(name = "getRegistry", versions = { "1.13", "1.14", "1.15", "1.16" })
  public Object getRegistry_1_13() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
    return getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "IRegistry").getField("ITEM").get(null);
  }

  @Version(name = "getRegistry", versions = { "1.17" })
  public Object getRegistry_1_17() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
    return getClassFromPath(Path.MINECRAFT_CORE, "IRegistry").getField("Z").get(null);
  }

  @Version(name = "getRegistry", versions = { "1.18", "1.19.1", "1.19.2" })
  public Object getRegistry_1_18_19() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
    final Class<?> registryBlockClass = getClassFromPath(Path.MINECRAFT_CORE, "RegistryBlocks");
    final Class<?> itemClass = getItemClass_latest();
    final Optional<Field> maybeRegistryField = Arrays
        .stream(getClassFromPath(Path.MINECRAFT_CORE, "IRegistry").getFields())
        .filter(f -> f.getType().isAssignableFrom(registryBlockClass)
            && ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0].equals(itemClass))
        .findFirst();
    if (!maybeRegistryField.isPresent()) {
      throw new NoSuchFieldException("Can't find item Registry.");
    }
    return maybeRegistryField.get().get(null);
  }

  @Version(name = "getRegistry")
  public Object getRegistry_latest() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
    return Class.forName("net.minecraftforge.registries.ForgeRegistries").getDeclaredField("ITEMS").get(null);
  }

  @Version(name = "getRegistryItem", versions = { "1.12" })
  public Object getRegistryItem_1_12(Object registry, Object minecraftKey)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return registry.getClass().getMethod("get", Object.class).invoke(registry, minecraftKey);
  }

  @Version(name = "getRegistryItem", versions = { "1.13", "1.14", "1.15", "1.16", "1.17" })
  public Object getRegistryItem_old(Object registry, Object minecraftKey)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return registry.getClass().getMethod("get", minecraftKey.getClass()).invoke(registry, minecraftKey);
  }

  @Version(name = "getRegistryItem")
  public Object getRegistryItem(Object registry, Object minecraftKey)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return registry.getClass().getMethod("getValue", minecraftKey.getClass()).invoke(registry, minecraftKey);
  }

  @Version(name = "getMinecraftKeyClass", versions = { "1.12", "1.13", "1.14", "1.15", "1.16" })
  public Class<?> getMinecraftKeyClass_old() throws ClassNotFoundException {
    return getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "MinecraftKey");
  }

  @Version(name = "getMinecraftKeyClass")
  public Class<?> getMinecraftKeyClass_latest() throws ClassNotFoundException {
    return getClassFromPath(Path.MINECRAFT_RESOURCES, "MinecraftKey");
  }

  @Version(name = "getMinecraftKey", versions = { "1.12" })
  public Object getMinecraftKey_1_12(Object registry, Object nmsItemStack)
      throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return registry.getClass().getMethod("b", Object.class).invoke(registry, invokeMethod(nmsItemStack, "getItem"));
  }

  @Version(name = "getMinecraftKey", versions = { "1.13", "1.14", "1.15", "1.16", "1.17" })
  public Object getMinecraftKey_1_13(Object registry, Object nmsItemStack)
      throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return registry.getClass().getMethod("getKey", Object.class).invoke(registry,
        invokeMethod(nmsItemStack, "getItem"));
  }

  @Version(name = "getMinecraftKey", versions = { "1.18", "1.19" })
  public Object getMinecraftKey_1_18(Object registry, Object nmsItemStack)
      throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return registry.getClass().getMethod("b", Object.class).invoke(registry, invokeMethod(nmsItemStack, "c"));
  }

  @Version(name = "getMinecraftKey")
  public Object getMinecraftKey_latest(Object registry, Object nmsItemStack)
      throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return registry.getClass().getMethod("b", Object.class).invoke(registry, invokeMethod(nmsItemStack, "d"));
  }

  @Version(name = "getNamespace", versions = { "1.14", "1.15", "1.16", "1.17" })
  public Object getNamespace_old(Object minecraftKey) {
    return invokeMethod(minecraftKey, "getNamespace");
  }

  @Version(name = "getNamespace")
  public Object getNamespace_latest(Object minecraftKey) {
    return invokeMethod(minecraftKey, "b");
  }

  @Version(name = "getKey", versions = { "1.12", "1.13", "1.14", "1.15", "1.16", "1.17" })
  public Object getKey_old(Object minecraftKey) {
    return invokeMethod(minecraftKey, "getKey");
  }

  @Version(name = "getKey")
  public Object getKey_latest(Object minecraftKey) {
    return invokeMethod(minecraftKey, "a");
  }

  @Version(name = "newNBTTagCompound", versions = { "1.12", "1.13", "1.14", "1.15", "1.16" })
  public Object newNBTTagCompound_old() throws ClassNotFoundException {
    return newInstance(getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "NBTTagCompound"));
  }

  @Version(name = "newNBTTagCompound")
  public Object newNBTTagCompound_latest() throws ClassNotFoundException {
    return newInstance(getClassFromPath(Path.MINECRAFT_NBT, "NBTTagCompound"));
  }

  @Version(name = "getItemClass", versions = { "1.12", "1.13", "1.14", "1.15", "1.16" })
  public Class<?> getItemClass_old() throws ClassNotFoundException {
    return getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "Item");
  }

  @Version(name = "getItemClass")
  public Class<?> getItemClass_latest() throws ClassNotFoundException {
    return getClassFromPath(Path.MINECRAFT_WORLD_ITEM, "Item");
  }

  @Version(name = "getName", versions = { "1.12", "1.13", "1.14", "1.15", "1.16", "1.17" })
  public Object getName_old(Object nmsItemStack) {
    return invokeMethod(nmsItemStack, "getName");
  }

  @Version(name = "getName")
  public Object getName_latest(Object nmsItemStack)
      throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    final Class<?> chatBaseComponent = getClassFromPath(Path.MINECRAFT_NETWORK_CHAT, "IChatBaseComponent");
    return VersionField.from(nmsItemStack).invokeMethodWithType(chatBaseComponent);
  }

  private String before1_18(String before, String after) {
    switch (Utils.getVersion()) {
      case "1.12":
      case "1.13":
      case "1.14":
      case "1.15":
      case "1.16":
      case "1.17":
        return before;
      default:
        return after;
    }
  }

  @Version(name = "getTag", versions = { "1.12", "1.13", "1.14", "1.15", "1.16", "1.17" })
  public Object getTagOld(Object itemStack) {
    return invokeMethod(itemStack, "getTag");
  }

  @Version(name = "getTag")
  public Object getTag(Object itemStack)
      throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
    final Class<?> nbtTagCompound = getClassFromPath(Path.MINECRAFT_NBT, "NBTTagCompound");
    final Optional<Method> maybeMethod = Arrays.stream(itemStack.getClass().getMethods())
        .filter(m -> m.getReturnType().isAssignableFrom(nbtTagCompound) && m.getParameters().length == 0)
        .findFirst();
    if (maybeMethod.isPresent())
      return maybeMethod.get().invoke(itemStack);
    return null;
  }

  public String hasTagName() {
    return before1_18("hasKey", "e");
  }

  public String setBooleanName() {
    return before1_18("setBoolean", "a");
  }

  public String setTagName() {
    return before1_18("setTag", "c");
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
      Class<?> minecraftKeyClass = call("getMinecraftKeyClass", this);
      Object minecraftKey = minecraftKeyClass.getConstructor(String.class).newInstance(name);

      Object registry = getRegistry();
      Object item = call("getRegistryItem", this, registry, minecraftKey);
      if (item == null)
        return null;

      Class<?> itemCLass = call("getItemClass", this);

      Method asNewCraftStack = getClassFromPathWithVersion(Path.BUKKIT, "inventory.CraftItemStack")
          .getDeclaredMethod("asNewCraftStack", itemCLass);
      ItemStack itemStack = (ItemStack) asNewCraftStack.invoke(null, item);

      return this.setNbtTag(itemStack);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Get the minecraftkey in string. Format minecraft:dirt
   *
   * @param itemStack
   *                  ItemStack
   * @return return the string minecraft key
   */
  public String getMinecraftKey(ItemStack itemStack) {
    try {
      Method asNMSCopy = getClassFromPathWithVersion(Path.BUKKIT, "inventory.CraftItemStack")
          .getDeclaredMethod("asNMSCopy", ItemStack.class);
      Object nmsItemStack = asNMSCopy.invoke(null, itemStack);
      Class<?> itemClass = Class.forName("net.minecraft.world.item.Item");
      Object item = VersionField.from(nmsItemStack).invokeMethodWithType(itemClass);

      Object registry = Class.forName("net.minecraftforge.registries.ForgeRegistries").getDeclaredField("ITEMS").get(null);
      Object resoureLocation = registry.getClass().getMethod("getKey", Object.class).invoke(registry, item);

      return resoureLocation.toString();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get the display name of an itemstack
   *
   * @param itemStack ItemStack
   * @return return the displayname
   */
  public String getItemStackDisplayName(ItemStack itemStack) {
    try {
      Method asNMSCopy = getClassFromPathWithVersion(Path.BUKKIT, "inventory.CraftItemStack")
          .getDeclaredMethod("asNMSCopy", ItemStack.class);
      Object nmsItemStack = asNMSCopy.invoke(null, itemStack);
      Object name = call("getName", this, nmsItemStack);

      if (name instanceof String) {
        return (String) name;
      }
      return invokeMethod(name, "getString").toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Update the inventory name update by the player
   *
   * @param title  The new inventory Name
   * @param player The player
   */
  @Version(name = "updateInventoryName", versions = { "1.12", "1.13" })
  public void updateInventoryName_1_13(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = newInstance(getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "ChatMessage"), title,
          new Object[] {});
      VersionField activeContainerVF = VersionField.from(entityPlayer).get("activeContainer");
      Object windowId = activeContainerVF.get("windowId").value();

      Class<?> ichat = getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "IChatBaseComponent");

      Object packet = getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, String.class, ichat, Integer.TYPE)
          .newInstance(windowId, "minecraft:chest", ichat.cast(chatMessage),
              player.getOpenInventory().getTopInventory().getSize());

      Object playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);

      playerConnection.getClass().getMethod("sendPacket", getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "Packet"))
          .invoke(playerConnection, packet);
      entityPlayer.getClass()
          .getMethod("updateInventory", getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "Container"))
          .invoke(entityPlayer, activeContainerVF.value());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Update the inventory name update by the player
   *
   * @param title  The new inventory Name
   * @param player The player
   */
  @Version(name = "updateInventoryName", versions = { "1.14", "1.15", "1.16" })
  public void updateInventoryName_old(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = newInstance(getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "ChatMessage"), title,
          new Object[] {});
      VersionField activeContainerVF = VersionField.from(entityPlayer).get("activeContainer");
      Object windowId = activeContainerVF.get("field_75152_c").value();

      Class<?> ichat = getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "IChatBaseComponent");
      Class<?> containers = getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "Containers");

      Object packet = getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("GENERIC_9X6").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);

      playerConnection.getClass().getMethod("sendPacket", getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "Packet"))
          .invoke(playerConnection, packet);
      entityPlayer.getClass()
          .getMethod("updateInventory", getClassFromPathWithVersion(Path.MINECRAFT_SERVER, "Container"))
          .invoke(entityPlayer, activeContainerVF.value());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Update the inventory name update by the player
   *
   * @param title  The new inventory Name
   * @param player The player
   */
  @Version(name = "updateInventoryName", versions = { "1.17" })
  public void updateInventoryName_1_17(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = newInstance(getClassFromPath(Path.MINECRAFT_NETWORK_CHAT, "ChatMessage"), title,
          new Object[] {});
      VersionField activeContainerVF = VersionField.from(entityPlayer).get("bV");
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = getClassFromPath(Path.MINECRAFT_NETWORK_CHAT, "IChatBaseComponent");
      Class<?> containers = getClassFromPath(Path.MINECRAFT_WORLD_INVENTORY, "Containers");

      Object packet = getClassFromPath(Path.MINECRAFT_NETWORK_GAME, "PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("b").get(entityPlayer);

      playerConnection.getClass().getMethod("sendPacket", getClassFromPath(Path.MINECRAFT_NETWORK_PROTOCOL, "Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Version(name = "updateInventoryName", versions = { "1.18" })
  public void updateInventoryName_1_18(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = newInstance(getClassFromPath(Path.MINECRAFT_NETWORK_CHAT, "ChatMessage"), title,
          new Object[] {});
      Class<?> containerClass = getClassFromPath(Path.MINECRAFT_WORLD_INVENTORY, "Container");
      VersionField activeContainerVF = VersionField.from(entityPlayer).getWithType(containerClass);
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = getClassFromPath(Path.MINECRAFT_NETWORK_CHAT, "IChatBaseComponent");
      Class<?> containers = getClassFromPath(Path.MINECRAFT_WORLD_INVENTORY, "Containers");

      Object packet = getClassFromPath(Path.MINECRAFT_NETWORK_GAME, "PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("b").get(entityPlayer);

      playerConnection.getClass().getMethod("a", getClassFromPath(Path.MINECRAFT_NETWORK_PROTOCOL, "Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Version(name = "updateInventoryName", versions = { "1.19" })
  public void updateInventoryName_1_19(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = getClassFromPath(Path.MINECRAFT_NETWORK_CHAT, "IChatBaseComponent")
          .getMethod("b", String.class).invoke(null, title);
      Class<?> containerClass = getClassFromPath(Path.MINECRAFT_WORLD_INVENTORY, "Container");
      VersionField activeContainerVF = VersionField.from(entityPlayer).getWithType(containerClass);
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = getClassFromPath(Path.MINECRAFT_NETWORK_CHAT, "IChatBaseComponent");
      Class<?> containers = getClassFromPath(Path.MINECRAFT_WORLD_INVENTORY, "Containers");

      Object packet = getClassFromPath(Path.MINECRAFT_NETWORK_GAME, "PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("b").get(entityPlayer);

      playerConnection.getClass().getMethod("a", getClassFromPath(Path.MINECRAFT_NETWORK_PROTOCOL, "Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Version(name = "updateInventoryName")
  public void updateInventoryName_latest(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = getClassFromPath(Path.MINECRAFT_NETWORK_CHAT, "IChatBaseComponent")
          .getMethod("b", String.class).invoke(null, title);
      Class<?> containerClass = getClassFromPath(Path.MINECRAFT_WORLD_INVENTORY, "Container");
      VersionField activeContainerVF = VersionField.from(entityPlayer).getWithType(containerClass);
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = getClassFromPath(Path.MINECRAFT_NETWORK_CHAT, "IChatBaseComponent");
      Class<?> containers = getClassFromPath(Path.MINECRAFT_WORLD_INVENTORY, "Containers");

      Object packet = getClassFromPath(Path.MINECRAFT_NETWORK_GAME, "PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("c").get(entityPlayer);

      playerConnection.getClass().getMethod("a", getClassFromPath(Path.MINECRAFT_NETWORK_PROTOCOL, "Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
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
      Method asNMSCopy = getClassFromPathWithVersion(Path.BUKKIT, "inventory.CraftItemStack")
          .getDeclaredMethod("asNMSCopy", ItemStack.class);
      Object nmsItemStack = asNMSCopy.invoke(null, itemStack);

      Object tagCompound = call("getTag", this, nmsItemStack);

      return tagCompound != null && (Boolean) invokeMethod(tagCompound, hasTagName(), this.NBTTAG);
    } catch (Exception e) {
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
    if (itemStack == null)
      return null;
    if (this.hasNbtTag(itemStack))
      return itemStack;

    try {
      Class<?> craftItemStack = getClassFromPathWithVersion(Path.BUKKIT, "inventory.CraftItemStack");
      Method asNMSCopy = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
      Object nmsItemStack = asNMSCopy.invoke(null, itemStack);

      Object tagCompound = call("getTag", this, nmsItemStack);

      if (tagCompound == null) {
        tagCompound = call("newNBTTagCompound", this);
      }

      tagCompound.getClass().getMethod(setBooleanName(), String.class, boolean.class).invoke(tagCompound, this.NBTTAG,
          true);

      invokeMethod(nmsItemStack, setTagName(), tagCompound);
      return (ItemStack) craftItemStack.getDeclaredMethod("asBukkitCopy", nmsItemStack.getClass()).invoke(null,
          nmsItemStack);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return itemStack;
  }

}
