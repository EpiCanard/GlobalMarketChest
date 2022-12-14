package fr.epicanard.globalmarketchest.utils.annotations;

import fr.epicanard.globalmarketchest.exceptions.MissingMethodException;
import fr.epicanard.globalmarketchest.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Class that allow to call method with annotations @Version
 */
public final class AnnotationCaller {

  /**
   * Get all methods of a class with the annotation <code>@Version.name = methodName</code>
   * And <code>@Version.value = server version or "latest"</code>
   *
   * @param methodName Name of annotation to search
   * @param objectClass Class on which find the methods
   * @return Return the matching method or the latest if missing
   */
  private static Method getMethod(String methodName, Class<?> objectClass) {
    final List<Method> methods = Arrays.asList(objectClass.getMethods());
    Method latest = null;

    for (Method method : methods) {
      final Version versionAno = method.getDeclaredAnnotation(Version.class);
      if (versionAno != null && versionAno.name().equals(methodName)) {
        final List<String> versions = Arrays.asList(versionAno.versions());
        if (versions.contains(Utils.getVersion()) || versions.contains(Utils.getFullVersion()))
          return method;
        if (versions.contains("latest"))
          latest = method;
      }
    }
    return latest;
  }

  /**
   * Search a method with annotation Version.name=@param methodName
   * And Version.versions=Server version or Version.versions=value
   *
   * This process allow to have multi version of one method tagged for each version
   *
   * Exemple:
   * <pre>
   * @Version(name="test", versions={"1.13"})
   * myMethod() {}
   *
   * AnnotationCaller.call("test", my_obj, null)
   * </pre>
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
  public static <T> T call(String methodName, Object object, Object... args) throws MissingMethodException {
    return AnnotationCaller.call(methodName, object.getClass(), object, args);
  }

  /**
   * Search a method with annotation Version.name=@param methodName
   * And Version.versions=Server version or Version.versions=value
   *
   * This process allow to have multi version of one method tagged for each version
   *
   * Exemple:
   * <pre>
   * @Version(name="test", versions={"1.13"})
   * myMethod() {}
   *
   * AnnotationCaller.call("test", my_obj, null)
   * </pre>
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
  public static <T> T call(String methodName, Class<?> objectClass, Object object, Object... args) throws MissingMethodException {
    final Method method = AnnotationCaller.getMethod(methodName, objectClass);

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

  private AnnotationCaller() {}
}
