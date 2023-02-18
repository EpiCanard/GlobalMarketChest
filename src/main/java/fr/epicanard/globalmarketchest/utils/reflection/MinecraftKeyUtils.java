package fr.epicanard.globalmarketchest.utils.reflection;

import fr.epicanard.globalmarketchest.utils.Version;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.*;

public abstract class MinecraftKeyUtils {

  private static MinecraftKeyUtils INSTANCE;

  /**
   * Get the minecraftkey in string. Format minecraft:dirt
   *
   * @param itemStack
   *                  ItemStack
   * @return return the string minecraft key
   */
  public static String getMinecraftKey(ItemStack itemStack) {
    try {
      Object nmsItemStack = NmsItemStackUtils.toNMSItemStack(itemStack);
      Object nmsItem = invokeMethod(nmsItemStack, getInstance().getNmsGetItem());
      Object minecraftKey = Registry.getRegistryKey(nmsItem);
      System.out.println(">> NIT " + nmsItemStack);
      System.out.println(">> IT " + nmsItem);
      System.out.println(">> MK " + minecraftKey);

      return invokeMethod(minecraftKey, getInstance().getNamespaceMethod()) + ":" + invokeMethod(minecraftKey, getInstance().getKeyMethod());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Singleton, method to get the instance of this class
   */
  private static MinecraftKeyUtils getInstance() {
    if (INSTANCE == null)
      if (Version.isEqualsTo(Version.V1_12))
        INSTANCE = new V1_12();
      else if (Version.isEqualsTo(Version.V1_13))
        INSTANCE = new V1_13();
      else if (Version.isLowerOrEqualsTo(Version.V1_17))
        INSTANCE = new V1_14();
      else
        INSTANCE = new V1_X();
    return INSTANCE;
  }

  protected abstract String getNamespaceMethod();

  protected abstract String getKeyMethod();

  protected abstract String getNmsGetItem();

  // 1.12
  @Getter
  private static class V1_12 extends MinecraftKeyUtils {
    private final String namespaceMethod = "b";
    private final String keyMethod = "getKey";
    private final String nmsGetItem = "getItem";
  }

  // 1.13
  @Getter
  private static class V1_13 extends MinecraftKeyUtils {
    private final String namespaceMethod = "b";
    private final String keyMethod = "getKey";
    private final String nmsGetItem = "getItem";
  }

  // 1.14 - 1.17
  @Getter
  private static class V1_14 extends MinecraftKeyUtils {
    private final String namespaceMethod = "getNamespace";
    private final String keyMethod = "getKey";
    private final String nmsGetItem = "getItem";
  }

  // 1.18+
  @Getter
  private static class V1_X extends MinecraftKeyUtils {
    private final String namespaceMethod = "b";
    private final String keyMethod = "a";
    private final String nmsGetItem = "c";
  }


}
