package fr.epicanard.globalmarketchest.utils.annotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.epicanard.globalmarketchest.exceptions.MissingMethodException;
import fr.epicanard.globalmarketchest.utils.Utils;

/**
 * Class that allow to call method with annotations @Version
 */
public final class AnnotationCaller {

  /**
   * Get all methods of a class with the annotation @Version.name = methodName
   * And @Version.value = server version or "latest"
   *
   * @param methodName Name of annotation to search
   * @param objectClass Class on which find the methods
   * @return Return a map of string and method where string is the version
   */
  private static Map<String, Method> getMethods(String methodName, Class<?> objectClass) {
    final List<Method> methods = Arrays.asList(objectClass.getMethods());
    final Map<String, Method> finalMethods = new HashMap<>();

    methods.stream().forEach((method) -> {
      final Version versionAno = method.getDeclaredAnnotation(Version.class);
      if (versionAno != null && versionAno.name().equals(methodName)) {
        final List<String> versions = Arrays.asList(versionAno.versions());
        if (versions.contains(Utils.getVersion()) || versions.contains("latest")) {
          versions.forEach(version -> {
            finalMethods.put(version, method);
          });
        }
      }
    });
    return finalMethods;
  }

  /**
   * Search a method with annotation Version.name=@param methodName
   * And Version.versions=Server version or Version.versions=value
   *
   * This process allow to have multi version of one method tagged for each version
   *
   * Exemple:
   * @Version(name="test", versions={"1.13"})
   * myMethod() {}
   *
   * AnnotationCaller.call("test", my_obj, null)
   *
   * See: fr.epicanard.globalmarketchest.utils.annotations.Version
   *
   * @param <T> Return type of called method
   * @param methodName Name of the annotation to find
   * @param object Object on which is declared the method
   * @param args Arguments for method call
   * @return Return what is return by the method called
   * @throws MissingMethodException Throw when method with name (methodName) is missing with the server version and latest
   */
  public static <T> T call(String methodName, Object object, Object ...args) throws MissingMethodException {
    return AnnotationCaller.call(methodName, object.getClass(), object, args);
  }

  /**
   * Search a method with annotation Version.name=@param methodName
   * And Version.versions=Server version or Version.versions=value
   *
   * This process allow to have multi version of one method tagged for each version
   *
   * Exemple:
   * @Version(name="test", versions={"1.13"})
   * myMethod() {}
   *
   * AnnotationCaller.call("test", my_obj, null)
   *
   * See: fr.epicanard.globalmarketchest.utils.annotations.Version
   *
   * @param <T> Return type of called method
   * @param methodName Name of the annotation to find
   * @param objectClass Class of object
   * @param object Object on which is declared the method
   * @param args Arguments for method call
   * @return Return what is return by the method called
   * @throws MissingMethodException Throw when method with name (methodName) is missing with the server version and latest
   */
  @SuppressWarnings("unchecked")
  public static <T> T call(String methodName, Class<?> objectClass, Object object, Object ...args) throws MissingMethodException {
    final Map<String, Method> methods = AnnotationCaller.getMethods(methodName, objectClass);
    final Method method = Optional.ofNullable(methods.get(Utils.getVersion())).orElse(methods.get("latest"));

    if (method == null) {
      throw new MissingMethodException(methodName);
    }
    try {
      return (T) method.invoke(object, args);
    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }
}
