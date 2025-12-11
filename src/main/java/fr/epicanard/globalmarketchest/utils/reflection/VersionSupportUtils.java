package fr.epicanard.globalmarketchest.utils.reflection;

import fr.epicanard.globalmarketchest.utils.Utils;
import fr.epicanard.globalmarketchest.utils.annotations.Version;
import fr.epicanard.globalmarketchest.utils.reflection.tags.ITagHandler;
import fr.epicanard.globalmarketchest.utils.reflection.tags.OldTagHandler;
import fr.epicanard.globalmarketchest.utils.reflection.tags.TagHandler;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Optional;

import static fr.epicanard.globalmarketchest.utils.annotations.AnnotationCaller.call;
import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.*;

public class VersionSupportUtils implements ITagHandler {

  private static VersionSupportUtils INSTANCE;

  private ITagHandler tagHandler;

  /**
   * Private constructor VersionSupportUtils
   */
  private VersionSupportUtils(ITagHandler handler) {
    this.tagHandler = handler;
  }

  /**
   * Singleton, method to get the instance of this class
   */
  public static VersionSupportUtils getInstance() {
    if (INSTANCE == null) {
      if (GlobalMarketChest.plugin.getMinecraftVersion().isLowerOrEqualsTo(1, 13))
        INSTANCE = new VersionSupportUtils(new OldTagHandler());
      else
        INSTANCE = new VersionSupportUtils(new TagHandler());
    }
    return INSTANCE;
  }

  // ======== TOOLS ============

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
    return Path.MINECRAFT_SERVER.getClass("Item").getField("REGISTRY").get(null);
  }

  @Version(name = "getRegistry", versions = { "1.13", "1.14", "1.15", "1.16" })
  public Object getRegistry_1_13() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
    return Path.MINECRAFT_SERVER.getClass("IRegistry").getField("ITEM").get(null);
  }

  @Version(name = "getRegistry", versions = { "1.17" })
  public Object getRegistry_1_17() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
    return Path.MINECRAFT_CORE.getClass("IRegistry").getField("Z").get(null);
  }

  @Version(name = "getRegistry", versions = { "1.18", "1.19.1", "1.19.2" })
  public Object getRegistry_1_18_19() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
    final Class<?> registryBlockClass = Path.MINECRAFT_CORE.getClass("RegistryBlocks");
    final Class<?> itemClass = getItemClass_latest();
    final Class<?> registryClass = Path.MINECRAFT_CORE.getClass("IRegistry");
    final Optional<Field> maybeRegistryField = findParametrizedField(registryClass, registryBlockClass, itemClass);

    if (!maybeRegistryField.isPresent()) {
      throw new NoSuchFieldException("Can't find item Registry.");
    }
    return maybeRegistryField.get().get(null);
  }

  @Version(name = "getRegistry")
  public Object getRegistry_latest() throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
    final Class<?> registryBlockClass = Path.MINECRAFT_CORE.getClass("RegistryBlocks");
    final Class<?> itemClass = getItemClass_latest();
    final Class<?> registryClass = Path.MINECRAFT_CORE_REGISTRIES.getClass("BuiltInRegistries");
    final Optional<Field> maybeRegistryField = findParametrizedField(registryClass, registryBlockClass, itemClass);

    if (!maybeRegistryField.isPresent()) {
      throw new NoSuchFieldException("Can't find item Registry.");
    }
    return maybeRegistryField.get().get(null);
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
    return registry.getClass().getMethod("a", minecraftKey.getClass()).invoke(registry, minecraftKey);
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

  @Version(name = "getMinecraftKey", versions = {"1.20.0, 1.20.1", "1.20.2", "1.20.3", "1.20.4"})
  public Object getMinecraftKey_1_20(Object registry, Object nmsItemStack)
      throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return registry.getClass().getMethod("b", Object.class).invoke(registry, invokeMethod(nmsItemStack, "d"));
  }

  @Version(name = "getMinecraftKey", versions = {"1.20.5, 1.20.6", "1.21.1"})
  public Object getMinecraftKey_1_21_1(Object registry, Object nmsItemStack)
      throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return registry.getClass().getMethod("b", Object.class).invoke(registry, invokeMethod(nmsItemStack, "g"));
  }

  @Version(name = "getMinecraftKey")
  public Object getMinecraftKey_latest(Object registry, Object nmsItemStack)
      throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return registry.getClass().getMethod("b", Object.class).invoke(registry, invokeMethod(nmsItemStack, "h"));
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

  @Version(name = "getItemClass", versions = { "1.12", "1.13", "1.14", "1.15", "1.16" })
  public Class<?> getItemClass_old() throws ClassNotFoundException {
    return Path.MINECRAFT_SERVER.getClass("Item");
  }

  @Version(name = "getItemClass")
  public Class<?> getItemClass_latest() throws ClassNotFoundException {
    return Path.MINECRAFT_WORLD_ITEM.getClass("Item");
  }

  @Version(name = "getName", versions = { "1.12", "1.13", "1.14", "1.15", "1.16", "1.17" })
  public Object getName_old(Object nmsItemStack) {
    return invokeMethod(nmsItemStack, "getName");
  }

  @Version(name = "getName")
  public Object getName_latest(Object nmsItemStack)
      throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    final Class<?> chatBaseComponent = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
    for (Method method: nmsItemStack.getClass().getMethods()) {
      if (method.getReturnType().isAssignableFrom(chatBaseComponent)) {
        Object result = method.invoke(nmsItemStack);
        if (result != null)
          return result;
      }
    }
    return null;
  }

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
  public ItemStack getItemStack(String name) {
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

  /**
   * Get the minecraftkey in string. Format minecraft:dirt
   *
   * @param itemStack
   *                  ItemStack
   * @return return the string minecraft key
   */
  public String getMinecraftKey(ItemStack itemStack) {
    try {
      Object nmsItemStack = NMSUtils.toNmsItemstack(itemStack);

      Object registry = getRegistry();
      Object minecraftKey = call("getMinecraftKey", this, registry, nmsItemStack);

      return call("getNamespace", this, minecraftKey) + ":" + call("getKey", this, minecraftKey);

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

  @Version(name = "updateInventoryName", versions = {  "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6", "1.21.1" })
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

  @Version(name = "updateInventoryName")
  public void updateInventoryName_latest(String title, Player player) {
    try {
      Object entityPlayer = invokeMethod(player, "getHandle");
      Class<?> entityHumanClass = Path.MINECRAFT_WORLD_ENTITY_PLAYER.getClass("EntityHuman");
      Object chatMessage = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent")
          .getMethod("b", String.class).invoke(null, title);
      Class<?> containerClass = Path.MINECRAFT_WORLD_INVENTORY.getClass("Container");
      VersionField activeContainerVF = VersionField.from(entityPlayer, entityHumanClass).getWithType(containerClass);
      Object windowId = activeContainerVF.get("l").value();

      Class<?> ichat = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
      Class<?> containers = Path.MINECRAFT_WORLD_INVENTORY.getClass("Containers");

      Object packet = Path.MINECRAFT_NETWORK_GAME.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, containers, ichat)
          .newInstance(windowId, containers.getField("f").get(null), ichat.cast(chatMessage));

      Class<?> playerConnectionClass = Path.MINECRAFT_SERVER_NETWORK.getClass("PlayerConnection");
      Object playerConnection = VersionField.from(entityPlayer).getWithType(playerConnectionClass).value();

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

}
