package fr.epicanard.globalmarketchest.utils.reflection;

import fr.epicanard.globalmarketchest.utils.Version;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.*;

public abstract class NmsItemStackUtils {
  private static final String NBTTAG = "GMCItem";
  private static NmsItemStackUtils INSTANCE;

  /**
   * Setup current utils
   */
  public static void setup() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    if (INSTANCE == null) {
      if (Version.isLowerThan(Version.V1_17)) {
        INSTANCE = new V1_12();
      } else if (Version.isEqualsTo(Version.V1_17)) {
        INSTANCE = new V1_17();
      } else {
        INSTANCE = new V1_X();
      }
    }
  }

  /**
   * Define if the GMC NBT TAG is set on this item
   *
   * @param itemStack Item to analyze
   * @return Return if the item as gmc nbt tag
   */
  public static boolean hasNbtTag(ItemStack itemStack) {
    try {
      Object nmsItemStack = toNMSItemStack(itemStack);
      Object tagCompound = getTag(nmsItemStack);

      return tagCompound != null && (Boolean) invokeMethod(tagCompound, getInstance().getHasKey(), NBTTAG);
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
  public static ItemStack setNbtTag(ItemStack itemStack) {
    if (itemStack == null)
      return null;
    if (hasNbtTag(itemStack))
      return itemStack;

    try {
      Object nmsItemStack = toNMSItemStack(itemStack);
      Object tagCompound = getTag(nmsItemStack);

      if (tagCompound == null) {
        tagCompound = newNBTTagCompound();
      }

      tagCompound.getClass().getMethod(getInstance().getSetBoolean(), String.class, boolean.class).invoke(tagCompound, NBTTAG, true);

      invokeMethod(nmsItemStack, getInstance().getSetTag(), tagCompound);
      return toCraftItemStack(nmsItemStack);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return itemStack;
  }

  /**
   * Get the display name of an itemstack
   *
   * @param itemStack ItemStack
   * @return return the displayname
   */
  public static String getItemStackDisplayName(ItemStack itemStack) {
    try {
      Object nmsItemStack = toNMSItemStack(itemStack);
      Object name = getInstance().getName(nmsItemStack);

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
   * Get an itemStack with is minecraft key
   *
   * @param name Name of Minecraft Key
   * @return
   */
  public static ItemStack getItemStack(String name) {
    try {
      Class<?> minecraftKeyClass = Class.forName(getInstance().getMinecraftKeyClass());
      Object minecraftKey = minecraftKeyClass.getConstructor(String.class).newInstance(name);

      Object item = Registry.getRegistryItem(minecraftKey);
      if (item == null)
        return null;

      Class<?> itemClass = Class.forName(getInstance().getItemClass());

      ItemStack itemStack = (ItemStack) VersionPath.BUKKIT
        .getClass("inventory.CraftItemStack")
        .getDeclaredMethod("asNewCraftStack", itemClass)
        .invoke(null, item);

      return NmsItemStackUtils.setNbtTag(itemStack);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Get an NMSItemStack from spigot ItemStack
   */
  public static Object toNMSItemStack(ItemStack itemStack) throws Exception {
    return VersionPath.BUKKIT.getClass("inventory.CraftItemStack")
      .getDeclaredMethod("asNMSCopy", ItemStack.class)
      .invoke(null, itemStack);
  }

  /**
   * Get a CraftItemStack from NMSItemStack
   */
  public static ItemStack toCraftItemStack(Object nmsItemStack) throws Exception {
    return (ItemStack) VersionPath.BUKKIT.getClass("inventory.CraftItemStack")
      .getDeclaredMethod("asBukkitCopy", nmsItemStack.getClass())
      .invoke(null, nmsItemStack);
  }

  /*
   * Call getTag method of NmsItemStacj
   */
  private static Object getTag(Object nmsItemStack) {
    return invokeMethod(nmsItemStack, getInstance().getGetTag());
  }

  private static NmsItemStackUtils getInstance() {
    if (INSTANCE == null) {
      throw new RuntimeException("NmsItemStackUtils is not initilialized. This should never happen.");
    }
    return INSTANCE;
  }

  /*
   * Create a new NBTTagCompound
   */
  private static Object newNBTTagCompound() throws ClassNotFoundException {
    return newInstance(Class.forName(getInstance().getNBTTagCompountPath()));
  }

  abstract String getGetTag();

  abstract String getNBTTagCompountPath();

  abstract String getHasKey();

  abstract String getSetBoolean();

  abstract String getSetTag();

  abstract Object getName(Object nmsItemStack) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException;

  abstract String getMinecraftKeyClass();

  abstract String getItemClass();

  /* ============================
   *      Implementation
   * ============================
   */

  // 1.12 - 1.16
  @Getter
  private static class V1_12 extends NmsItemStackUtils {
    private final String NBTTagCompountPath = VersionPath.MINECRAFT_SERVER.getPath("NBTTagCompound");
    private final String hasKey = "hasKey";
    private final String setBoolean = "setBoolean";
    private final String setTag = "setTag";
    private final String getTag = "getTag";
    private final String minecraftKeyClass = VersionPath.MINECRAFT_SERVER.getPath("MinecraftKey");
    private final String itemClass = VersionPath.MINECRAFT_SERVER.getPath("Item");

    @Override
    Object getName(Object nmsItemStack) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
      return invokeMethod(nmsItemStack, "getName");
    }
  }

  // 1.17
  @Getter
  private static class V1_17 extends V1_12 {
    private final String NBTTagCompountPath = VersionPath.MINECRAFT_NBT.getPath("NBTTagCompound");
    private final String minecraftKeyClass = VersionPath.MINECRAFT_RESOURCES.getPath("MinecraftKey");
    private final String itemClass = VersionPath.MINECRAFT_WORLD_ITEM.getPath("Item");
  }

  // 1.18+
  @Getter
  private static class V1_X extends NmsItemStackUtils {

    private final String NBTTagCompountPath = VersionPath.MINECRAFT_NBT.getPath("NBTTagCompound");
    private final String hasKey = "e";
    private final String setBoolean = "a";
    private final String setTag = "c";
    private final String getTag;
    private final String minecraftKeyClass = VersionPath.MINECRAFT_RESOURCES.getPath("MinecraftKey");
    private final String itemClass = VersionPath.MINECRAFT_WORLD_ITEM.getPath("Item");

    public V1_X() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
      final Class<?> nbtTagCompound = VersionPath.MINECRAFT_NBT.getClass("NBTTagCompound");
      final Class<?> nmsItemStack = VersionPath.MINECRAFT_WORLD_ITEM.getClass("ItemStack");
      final Optional<Method> maybeMethod = Arrays.stream(nmsItemStack.getMethods())
        .filter(m -> m.getReturnType().isAssignableFrom(nbtTagCompound) && m.getParameters().length == 0)
        .findFirst();
      if (maybeMethod.isPresent())
        this.getTag = maybeMethod.get().getName();
      else
        throw new NoSuchMethodException("No method found that can return NBTTagCompound");
    }

    @Override
    Object getName(Object nmsItemStack)
      throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
      final Class<?> chatBaseComponent = VersionPath.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
      return VersionField.from(nmsItemStack).invokeMethodWithType(chatBaseComponent);
    }
  }

}
