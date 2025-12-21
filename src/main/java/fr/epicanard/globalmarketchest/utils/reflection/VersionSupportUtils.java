package fr.epicanard.globalmarketchest.utils.reflection;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.MinecraftVersion;
import fr.epicanard.globalmarketchest.utils.annotations.Version;
import fr.epicanard.globalmarketchest.utils.reflection.minecraftkey.IMinecraftKeyHandler;
import fr.epicanard.globalmarketchest.utils.reflection.minecraftkey.MinecraftKeyHandler;
import fr.epicanard.globalmarketchest.utils.reflection.minecraftkey.NMSMinecraftKeyHandler;
import fr.epicanard.globalmarketchest.utils.reflection.tags.ITagHandler;
import fr.epicanard.globalmarketchest.utils.reflection.tags.OldTagHandler;
import fr.epicanard.globalmarketchest.utils.reflection.tags.TagHandler;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.*;

import static fr.epicanard.globalmarketchest.utils.annotations.AnnotationCaller.call;
import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.*;

public class VersionSupportUtils extends RegistryProvider implements ITagHandler, IMinecraftKeyHandler {

  private static VersionSupportUtils INSTANCE;

  private ITagHandler tagHandler;
  private IMinecraftKeyHandler minecraftKeyHandler;

  /**
   * Private constructor VersionSupportUtils
   */
  private VersionSupportUtils(ITagHandler tagHandler, IMinecraftKeyHandler minecraftKeyHandler) {
    this.tagHandler = tagHandler;
    this.minecraftKeyHandler = minecraftKeyHandler;
  }

  /**
   * Singleton, method to get the instance of this class
   */
  public static VersionSupportUtils getInstance() {
    if (INSTANCE == null) {
      final MinecraftVersion version = GlobalMarketChest.plugin.getMinecraftVersion();
      final ITagHandler tagHandler = (version.isLowerOrEqualsTo(1, 13)) ? new OldTagHandler() : new TagHandler();
      final IMinecraftKeyHandler minecraftKeyHandler = (version.isLowerThan(1, 13)) ?  new NMSMinecraftKeyHandler() : new MinecraftKeyHandler();

      INSTANCE = new VersionSupportUtils(tagHandler, minecraftKeyHandler);
    }
    return INSTANCE;
  }

  // ======== TOOLS ============

  @Version(name = "newMinecraftKey", versions = { "1.12", "1.13", "1.14", "1.15", "1.16" })
  public Object newMinecraftKey_old(String name) throws Exception {
    return Path.MINECRAFT_SERVER.getClass("MinecraftKey").getConstructor(String.class).newInstance(name);
  }

  @Version(name = "newMinecraftKey", versions = { "1.17", "1.18", "1.19", "1.20" })
  public Object newMinecraftKey_1_17(String name) throws Exception {
    return Path.MINECRAFT_RESOURCES.getClass("MinecraftKey").getConstructor(String.class).newInstance(name);
  }

  @Version(name = "newMinecraftKey")
  public Object newMinecraftKey_latest(String name) throws Exception {
    return Path.MINECRAFT_RESOURCES.getClass("MinecraftKey").getMethod("a", String.class).invoke(null, name);
  }

  // ======= SPECIFIC METHOD ===========

  /**
   * Get an itemStack with is minecraft key
   *
   * @param name
   * @return
   */
  private ItemStack getItemStackFromBukkit(String name) {
    try {
      Object minecraftKey = call("newMinecraftKey", this, name);;

      Object registry = getRegistry();
      Object item = call("getRegistryItem", this, registry, minecraftKey);
      if (item == null)
        return null;

      Class<?> itemCLass = call("getItemClass", this);

      Method asNewCraftStack = Path.BUKKIT.getClass("inventory.CraftItemStack")
          .getDeclaredMethod("asNewCraftStack", itemCLass);
      ItemStack itemStack = (ItemStack) asNewCraftStack.invoke(null, item);

      return this.setTag(itemStack);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public ItemStack getItemStack(String name) {
    if (name.startsWith("minecraft:") && GlobalMarketChest.plugin.getMinecraftVersion().isHigherThan(1, 13)) {
      String key = name.substring(10);
      Material material = Registry.MATERIAL.get(NamespacedKey.minecraft(key));
      return this.setTag(new ItemStack(material, 1));
    } else {
      return getItemStackFromBukkit(name);
    }
  }


  /**
   * Get the display name of an itemstack
   *
   * @param itemStack ItemStack
   * @return return the displayname
   */
  public String getItemStackDisplayName(ItemStack itemStack) {
    try {
      Method asNMSCopy = Path.BUKKIT.getClass("inventory.CraftItemStack")
          .getDeclaredMethod("asNMSCopy", ItemStack.class);
      Object nmsItemStack = asNMSCopy.invoke(null, itemStack);
      Object name = call("getName", this, nmsItemStack);

      if (name == null)
        return itemStack.getType().name();
      if (name instanceof String)
        return (String) name;
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
      Object chatMessage = newInstance(Path.MINECRAFT_SERVER.getClass("ChatMessage"), title,
          new Object[] {});
      VersionField activeContainerVF = VersionField.from(entityPlayer).get("activeContainer");
      Object windowId = activeContainerVF.get("windowId").value();

      Class<?> ichat = Path.MINECRAFT_SERVER.getClass("IChatBaseComponent");

      Object packet = Path.MINECRAFT_SERVER.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, String.class, ichat, Integer.TYPE)
          .newInstance(windowId, "minecraft:chest", ichat.cast(chatMessage), 54);

      Object playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);

      playerConnection.getClass().getMethod("sendPacket", Path.MINECRAFT_SERVER.getClass("Packet"))
          .invoke(playerConnection, packet);
      entityPlayer.getClass()
          .getMethod("updateInventory", Path.MINECRAFT_SERVER.getClass("Container"))
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
      Object chatMessage = newInstance(Path.MINECRAFT_SERVER.getClass("ChatMessage"), title,
          new Object[] {});
      VersionField activeContainerVF = VersionField.from(entityPlayer).get("activeContainer");
      Object windowId = activeContainerVF.get("windowId").value();

      Class<?> ichat = Path.MINECRAFT_SERVER.getClass("IChatBaseComponent");
      Class<?> containers = Path.MINECRAFT_SERVER.getClass("Containers");

      Object packet = Path.MINECRAFT_SERVER.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("GENERIC_9X6").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);

      playerConnection.getClass().getMethod("sendPacket", Path.MINECRAFT_SERVER.getClass("Packet"))
          .invoke(playerConnection, packet);
      entityPlayer.getClass()
          .getMethod("updateInventory", Path.MINECRAFT_SERVER.getClass("Container"))
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
      Object chatMessage = newInstance(Path.MINECRAFT_NETWORK_CHAT.getClass("ChatMessage"), title,
          new Object[] {});
      VersionField activeContainerVF = VersionField.from(entityPlayer).get("bV");
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
      Class<?> containers = Path.MINECRAFT_WORLD_INVENTORY.getClass("Containers");

      Object packet = Path.MINECRAFT_NETWORK_GAME.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("b").get(entityPlayer);

      playerConnection.getClass().getMethod("sendPacket", Path.MINECRAFT_NETWORK_PROTOCOL.getClass("Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Version(name = "updateInventoryName", versions = { "1.18" })
  public void updateInventoryName_1_18(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = newInstance(Path.MINECRAFT_NETWORK_CHAT.getClass("ChatMessage"), title,
          new Object[] {});
      Class<?> containerClass = Path.MINECRAFT_WORLD_INVENTORY.getClass("Container");
      VersionField activeContainerVF = VersionField.from(entityPlayer).getWithType(containerClass);
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
      Class<?> containers = Path.MINECRAFT_WORLD_INVENTORY.getClass("Containers");

      Object packet = Path.MINECRAFT_NETWORK_GAME.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("b").get(entityPlayer);

      playerConnection.getClass().getMethod("a", Path.MINECRAFT_NETWORK_PROTOCOL.getClass("Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Version(name = "updateInventoryName", versions = { "1.19" })
  public void updateInventoryName_1_19(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent")
          .getMethod("b", String.class).invoke(null, title);
      Class<?> containerClass = Path.MINECRAFT_WORLD_INVENTORY.getClass("Container");
      VersionField activeContainerVF = VersionField.from(entityPlayer).getWithType(containerClass);
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
      Class<?> containers = Path.MINECRAFT_WORLD_INVENTORY.getClass("Containers");

      Object packet = Path.MINECRAFT_NETWORK_GAME.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("b").get(entityPlayer);

      playerConnection.getClass().getMethod("a", Path.MINECRAFT_NETWORK_PROTOCOL.getClass("Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Version(name = "updateInventoryName", versions = { "1.20.0", "1.20.1" })
  public void updateInventoryName_latest_1_20(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Object chatMessage = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent")
          .getMethod("b", String.class).invoke(null, title);
      Class<?> containerClass = Path.MINECRAFT_WORLD_INVENTORY.getClass("Container");
      VersionField activeContainerVF = VersionField.from(entityPlayer).getWithType(containerClass);
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
      Class<?> containers = Path.MINECRAFT_WORLD_INVENTORY.getClass("Containers");

      Object packet = Path.MINECRAFT_NETWORK_GAME.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("c").get(entityPlayer);

      playerConnection.getClass().getMethod("a", Path.MINECRAFT_NETWORK_PROTOCOL.getClass("Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Version(name = "updateInventoryName", versions = {  "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6"})
  public void updateInventoryName_1_21_1(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Class<?> entityHumanClass = Path.MINECRAFT_WORLD_ENTITY_PLAYER.getClass("EntityHuman");
      Object chatMessage = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent")
          .getMethod("b", String.class).invoke(null, title);
      Class<?> containerClass = Path.MINECRAFT_WORLD_INVENTORY.getClass("Container");
      VersionField activeContainerVF = VersionField.from(entityPlayer, entityHumanClass).getWithType(containerClass);
      Object windowId = activeContainerVF.get("j").value();

      Class<?> ichat = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
      Class<?> containers = Path.MINECRAFT_WORLD_INVENTORY.getClass("Containers");

      Object packet = Path.MINECRAFT_NETWORK_GAME.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Object playerConnection = entityPlayer.getClass().getDeclaredField("c").get(entityPlayer);

      playerConnection.getClass().getMethod("b", Path.MINECRAFT_NETWORK_PROTOCOL.getClass("Packet"))
          .invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Optional<Field> findParametrizedField(Class<?> main, Class<?> type, Class<?> generic) {
    return Arrays
      .stream(main.getFields())
      .filter(f -> f.getType().isAssignableFrom(type)
          && ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0].equals(generic))
      .findFirst();
  }

  @Override
  public boolean hasTag(ItemStack itemStack) {
    return this.tagHandler.hasTag(itemStack);
  }

  @Override
  public ItemStack setTag(ItemStack itemStack) {
    return this.tagHandler.setTag(itemStack);
  }

  @Override
  public String getMinecraftKey(ItemStack itemStack) {
    return this.minecraftKeyHandler.getMinecraftKey(itemStack);
  }

}
