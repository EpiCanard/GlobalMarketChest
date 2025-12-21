package fr.epicanard.globalmarketchest.utils.reflection.minecraftkey;

import fr.epicanard.globalmarketchest.utils.annotations.Version;
import fr.epicanard.globalmarketchest.utils.reflection.NMSUtils;
import fr.epicanard.globalmarketchest.utils.reflection.RegistryProvider;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.*;


import static fr.epicanard.globalmarketchest.utils.annotations.AnnotationCaller.call;
import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.*;

public class NMSMinecraftKeyHandler extends RegistryProvider implements IMinecraftKeyHandler {

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

  /**
   * Get the minecraftkey in string. Format minecraft:dirt
   *
   * @param itemStack
   *                  ItemStack
   * @return return the string minecraft key
   */
  public @Override String getMinecraftKey(ItemStack itemStack) {
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
}
