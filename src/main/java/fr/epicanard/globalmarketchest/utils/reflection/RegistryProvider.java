package fr.epicanard.globalmarketchest.utils.reflection;

import fr.epicanard.globalmarketchest.utils.annotations.Version;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;

import static fr.epicanard.globalmarketchest.utils.annotations.AnnotationCaller.call;
import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.invokeMethod;

public abstract class RegistryProvider {

  /**
   * Get the static object Item.REGISTRY
   *
   * @return return the static object
   */
  protected Object getRegistry() {
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
    Class<?> chatBaseComponent;
    try {
      chatBaseComponent = Path.MINECRAFT_NETWORK_CHAT.getClass("IChatBaseComponent");
    } catch (ClassNotFoundException e) {
      chatBaseComponent = Path.MINECRAFT_NETWORK_CHAT.getClass("Component");
    }
    for (Method method: nmsItemStack.getClass().getMethods()) {
      if (method.getReturnType().isAssignableFrom(chatBaseComponent) && method.getParameterCount() == 0) {
        Object result = method.invoke(nmsItemStack);
        if (result != null)
          return result;
      }
    }
    return null;
  }

  private Optional<Field> findParametrizedField(Class<?> main, Class<?> type, Class<?> generic) {
    return Arrays
      .stream(main.getFields())
      .filter(f -> f.getType().isAssignableFrom(type)
          && ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0].equals(generic))
      .findFirst();
  }
}
