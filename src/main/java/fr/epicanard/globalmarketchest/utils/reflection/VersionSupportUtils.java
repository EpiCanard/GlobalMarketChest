package fr.epicanard.globalmarketchest.utils.reflection;

import fr.epicanard.globalmarketchest.utils.Utils;
import fr.epicanard.globalmarketchest.utils.annotations.Version;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Optional;

import static fr.epicanard.globalmarketchest.utils.annotations.AnnotationCaller.call;
import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.*;

public class VersionSupportUtils {

  private final String NBTTAG = "GMCItem";

  private static VersionSupportUtils INSTANCE;

  /**
   * Private constructor VersionSupportUtils
   */
  private VersionSupportUtils() {
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

  @Version(name = "getMinecraftKeyClass", versions = { "1.12", "1.13", "1.14", "1.15", "1.16" })
  public Class<?> getMinecraftKeyClass_old() throws ClassNotFoundException {
    return Path.MINECRAFT_SERVER.getClass("MinecraftKey");
  }

  @Version(name = "getMinecraftKeyClass")
  public Class<?> getMinecraftKeyClass_latest() throws ClassNotFoundException {
    return Path.MINECRAFT_RESOURCES.getClass("MinecraftKey");
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

  @Version(name = "getMinecraftKey")
  public Object getMinecraftKey_latest(Object registry, Object nmsItemStack)
      throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return registry.getClass().getMethod("b", Object.class).invoke(registry, invokeMethod(nmsItemStack, "g"));
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
    return newInstance(Path.MINECRAFT_SERVER.getClass("NBTTagCompound"));
  }

  @Version(name = "newNBTTagCompound")
  public Object newNBTTagCompound_latest() throws ClassNotFoundException {
    return newInstance(Path.MINECRAFT_NBT.getClass("NBTTagCompound"));
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

  @Version(name = "getTag", versions = { "1.18", "1.19", "1.20.0", "1.20.1", "1.20.2", "1.20.3", "1.20.4" })
  public Object getTag(Object itemStack)
      throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
    final Class<?> nbtTagCompound = Path.MINECRAFT_NBT.getClass("NBTTagCompound");
    final Optional<Method> maybeMethod = Arrays.stream(itemStack.getClass().getMethods())
        .filter(m -> m.getReturnType().isAssignableFrom(nbtTagCompound) && m.getParameters().length == 0)
        .findFirst();
    if (maybeMethod.isPresent())
      return maybeMethod.get().invoke(itemStack);
    return null;
  }

  @Version(name = "getTag")
  public Object getTagLatest(Object itemStack)
    throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
    // net.minecraft.core.component.DataComponents.CUSTOM_DATA => DataComponents.b ?
    final Class<?> dataComponentTypeClass = Path.MINECRAFT_CORE_COMPONENT.getClass("DataComponentType");
    final Class<?> dataComponentHolderClass = Path.MINECRAFT_CORE_COMPONENT.getClass("DataComponentHolder");
    final Class<?> customDataClass = Path.MINECRAFT_WORLD_ITEM_COMPONENT.getClass("CustomData");
    final Class<?> dataComponentsClass = Path.MINECRAFT_CORE_COMPONENT.getClass("DataComponents");
    // Get DataComponents.CUSTOM_DATA: DataComponentType<CustomData>
    final Optional<Field> customDataComponent = findParametrizedField(dataComponentsClass, dataComponentTypeClass, customDataClass);

    if (!customDataComponent.isPresent())
      return null;

    // Get Method <T> T get(DataComponentType<CustomData>)
    final Optional<Method> maybeMethod = Arrays.stream(dataComponentHolderClass.getMethods())
      .filter(m -> {
        return m.getGenericReturnType().getTypeName().equals("T") && m.getParameters().length == 1
          && m.getParameters()[0].getType().isAssignableFrom(dataComponentTypeClass);
      })
      .findFirst();
    if (maybeMethod.isPresent())
      return maybeMethod.get().invoke(itemStack, customDataComponent.get().get(null));
    return null;
  }

  @Version(name = "hasTagName", versions = { "1.12", "1.13", "1.14", "1.15", "1.17" })
  public String hasTagNameBefore1_18() {
    return "hasKey"; // NBTTagCompound.hasKey
  }
  @Version(name = "hasTagName", versions = { "1.18", "1.19", "1.20.0", "1.20.1", "1.20.2", "1.20.3", "1.20.4" })
  public String hasTagNameAfter1_19_20() {
    return "e"; // NBTTagCompound.e
  }
  @Version(name = "hasTagName")
  public String hasTagNameLatest() {
    return "a"; // CustomData.a
  }

  public String setBooleanName() {
    return before1_18("setBoolean", "a");
  }

  @Version(name = "setTag", versions = { "1.12", "1.13", "1.14", "1.15", "1.17" })
  public void setTagBefore1_18(Object nmsItemstack, Object tagCompound) {
    invokeMethod(nmsItemstack, "setTag", tagCompound);
  }

  @Version(name = "setTag", versions = { "1.18", "1.19", "1.20.0", "1.20.1", "1.20.2", "1.20.3", "1.20.4" })
  public void setTagAfter1_18(Object nmsItemstack, Object tagCompound) {
    invokeMethod(nmsItemstack, "c", tagCompound);
  }

  @Version(name = "setTag")
  public void setTagLatest(Object nmsItemStack, Object tagCompound) {
    try {

      final Class<?> dataComponentTypeClass = Path.MINECRAFT_CORE_COMPONENT.getClass("DataComponentType");
      final Class<?> customDataClass = Path.MINECRAFT_WORLD_ITEM_COMPONENT.getClass("CustomData");
      final Class<?> dataComponentsClass = Path.MINECRAFT_CORE_COMPONENT.getClass("DataComponents");
      final Object customDataComponent = findParametrizedField(dataComponentsClass, dataComponentTypeClass, customDataClass).get().get(null);

      // CustomData.set(DataComponents.CUSTOM_DATA, nmsItemStack, tagCompound)
      customDataClass
        .getMethod("a", dataComponentTypeClass, nmsItemStack.getClass(), Path.MINECRAFT_NBT.getClass("NBTTagCompound"))
        .invoke(null, customDataComponent, nmsItemStack, tagCompound);
    } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException  e) {
      e.printStackTrace();
    }
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

      Method asNewCraftStack = Path.BUKKIT.getClass("inventory.CraftItemStack")
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
      Object chatMessage = newInstance(Path.MINECRAFT_SERVER.getClass("ChatMessage"), title,
          new Object[] {});
      VersionField activeContainerVF = VersionField.from(entityPlayer).get("activeContainer");
      Object windowId = activeContainerVF.get("windowId").value();

      Class<?> ichat = Path.MINECRAFT_SERVER.getClass("IChatBaseComponent");

      Object packet = Path.MINECRAFT_SERVER.getClass("PacketPlayOutOpenWindow")
          .getConstructor(Integer.TYPE, String.class, ichat, Integer.TYPE)
          .newInstance(windowId, "minecraft:chest", ichat.cast(chatMessage),
              player.getOpenInventory().getTopInventory().getSize());

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

  @Version(name = "updateInventoryName")
  public void updateInventoryName_latest(String title, Player player) {
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

  /**
   * Define if the GMC NBT TAG is set on this item
   *
   * @param itemStack Item to analyze
   * @return Return if the item as gmc nbt tag
   */
  public boolean hasNbtTag(ItemStack itemStack) {
    try {
      Object nmsItemStack = NMSUtils.toNmsItemstack(itemStack);

      Object tagCompound = call("getTag", this, nmsItemStack);

      return tagCompound != null && (Boolean) invokeMethod(tagCompound, call("hasTagName", this), this.NBTTAG);
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
      Object nmsItemStack = NMSUtils.toNmsItemstack(itemStack);

      Object tagCompound = call("getTag", this, nmsItemStack);

      if (tagCompound == null)
        tagCompound = call("newNBTTagCompound", this);

      tagCompound.getClass().getMethod(setBooleanName(), String.class, boolean.class).invoke(tagCompound, this.NBTTAG, true);

      call("setTag", this, nmsItemStack, tagCompound);

      return NMSUtils.toItemstack(nmsItemStack);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return itemStack;
  }

  private Optional<Field> findParametrizedField(Class<?> main, Class<?> type, Class<?> generic) {
    return Arrays
      .stream(main.getFields())
      .filter(f -> f.getType().isAssignableFrom(type)
          && ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0].equals(generic))
      .findFirst();

  }

}
