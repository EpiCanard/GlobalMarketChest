package fr.epicanard.globalmarketchest.utils.reflection;

import fr.epicanard.globalmarketchest.utils.Version;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;

import static fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils.*;

public abstract class Registry {
  private static Registry INSTANCE;

  public static void setup() throws ClassNotFoundException, NoSuchFieldException  {
    if (INSTANCE == null) {
      if (Version.isEqualsTo(Version.V1_12))
        INSTANCE = new V1_12();
      else if (Version.isLowerThan(Version.V1_17))
        INSTANCE = new V1_13();
      else if (Version.isEqualsTo(Version.V1_17))
        INSTANCE = new V1_17();
      else
        INSTANCE = new V1_X();
    }
  }

  private static Registry getInstance() {
    if (INSTANCE == null) {
      throw new RuntimeException("Registry is not initilialized. This should never happen.");
    }
    return INSTANCE;
  }

  public static Object getRegistryItem(Object minecraftKey) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    Object registry = getInstance().getRegistry();
    Class<?> argClass = Version.isEqualsTo(Version.V1_12) ? Object.class : minecraftKey.getClass();
    return invokeMethod(registry, registry.getClass().getMethod(getInstance().getRegistryMethod(), argClass), minecraftKey);
  }


  public static Object getRegistryKey(Object value) throws IllegalAccessException, NoSuchMethodException {
    Object registry = getInstance().getRegistry();
    return invokeMethod(registry, registry.getClass().getMethod(getInstance().getGetKeyMethod(), Object.class), value);
  }

  private Object getRegistry() throws IllegalAccessException {
    return getRegistryField().get(null);
  }

  protected abstract String getRegistryMethod();

  protected abstract String getGetKeyMethod();

  protected abstract Field getRegistryField();

  /* ============================
   *      Implementation
   * ============================
   */

  // 1.12
  @Getter
  private static class V1_12 extends Registry {
    private final String registryMethod = "get";
    private final String getKeyMethod = "b";
    private final Field registryField;

    public V1_12() throws ClassNotFoundException, NoSuchFieldException {
      this.registryField = VersionPath.MINECRAFT_SERVER.getClass("Item").getField("REGISTRY");
    }

  }

  // 1.13 - 1.16
  @Getter
  private static class V1_13 extends Registry {
    private final String registryMethod = "get";
    private final String getKeyMethod = "getKey";
    private final Field registryField;

    public V1_13() throws ClassNotFoundException, NoSuchFieldException {
      this.registryField = VersionPath.MINECRAFT_SERVER.getClass("IRegistry").getField("ITEM");
    }
  }

  // 1.17
  @Getter
  private static class V1_17 extends Registry {
    private final String registryMethod = "get";
    private final String getKeyMethod = "getKey";
    private final Field registryField;

    public V1_17() throws ClassNotFoundException, NoSuchFieldException {
      this.registryField = VersionPath.MINECRAFT_SERVER.getClass("IRegistry").getField("Z");
    }
  }

  // 1.18+
  @Getter
  private static class V1_X extends Registry {
    private final String registryMethod = "a";
    private final String getKeyMethod = "b";
    private final Field registryField;

    public V1_X() throws ClassNotFoundException, NoSuchFieldException {
      Class<?> registryClass;
      if (Version.isLowerThan(Version.V1_19_3)) {
        System.out.println("LOWER THAN 1.19.3 " + Version.CURRENT.toStr());
        registryClass = VersionPath.MINECRAFT_CORE.getClass("IRegistry");
      } else {
        System.out.println("HIGHER THAN 1.19.3");
        registryClass = VersionPath.MINECRAFT_CORE_REGISTRIES.getClass("BuiltInRegistries");
      }
      final Class<?> registryBlockClass = VersionPath.MINECRAFT_CORE.getClass("RegistryBlocks");
      final Class<?> itemClass = VersionPath.MINECRAFT_WORLD_ITEM.getClass("Item");
      final Optional<Field> maybeRegistryField = Arrays
        .stream(registryClass.getFields())
        .filter(f -> f.getType().isAssignableFrom(registryBlockClass)
            && ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0].equals(itemClass))
        .findFirst();
      if (!maybeRegistryField.isPresent()) {
        throw new NoSuchFieldException("Can't find item Registry.");
      }
      this.registryField = maybeRegistryField.get();
    }
  }
}
