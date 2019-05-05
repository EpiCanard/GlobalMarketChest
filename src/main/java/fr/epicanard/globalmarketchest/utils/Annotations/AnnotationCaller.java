package fr.epicanard.globalmarketchest.utils.Annotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.epicanard.globalmarketchest.exceptions.MissingMethodException;
import fr.epicanard.globalmarketchest.utils.Utils;
import lombok.experimental.UtilityClass;

/**
 * Class that allow to call method with annotations @MethodName and @Version
 */
@UtilityClass
public class AnnotationCaller {

  /**
   * Get all methods of a class with the annotation @MethodName.value = methodName
   * And @Version.value = server version or "latest"
   * 
   * @param methodName Name of annotation to search
   * @param objectClass Class on which find the methods
   * @return Return a map of string and method where string is the version
   */
  private Map<String, Method> getMethods(String methodName, Class<?> objectClass) {
    final List<Method> methods = Arrays.asList(objectClass.getMethods());
    final Map<String, Method> finalMethods = new HashMap<>();

    methods.stream().forEach((method) -> {
      final MethodName methodAno = method.getDeclaredAnnotation(MethodName.class);
      if (methodAno != null && methodAno.value().equals(methodName)) {
        final Version versionAno = method.getDeclaredAnnotation(Version.class);
 
        if (versionAno != null && (versionAno.value().equals(Utils.getVersion()) || versionAno.value().equals("latest"))) {
          finalMethods.put(versionAno.value(), method);
        }
      }
    });

    return finalMethods;

  }

  /**
   * Search a method with annotation MethodName.value=@param methodName
   * And Version.value=Server version or Version.value=value
   * 
   * This process allow to have multi version of one method tagged for each version
   * 
   * Exemple:
   * @MethodName("test")
   * @Version("1.13")
   * myMethod() {}
   * 
   * AnnotationCaller.call("test", my_obj, null)
   * 
   * See: fr.epicanard.globalmarketchest.utils.Annotations.MethodName
   * See: fr.epicanard.globalmarketchest.utils.Annotations.Version
   * 
   * @param <T> Return type of called method
   * @param methodName Name of the annotation to find
   * @param object Object on which is declared the method
   * @param args Arguments for method call
   * @return Return what is return by the method called
   * @throws MissingMethodException Throw when method with name (methodName) is missing with the server version and latest
   */
  @SuppressWarnings("unchecked")
  public <T> T call(String methodName, Object object, Object ...args) throws MissingMethodException {
    return AnnotationCaller.call(methodName, object.getClass(), object, args);
  }

  /**
   * Search a method with annotation MethodName.value=@param methodName
   * And Version.value=Server version or Version.value=value
   * 
   * This process allow to have multi version of one method tagged for each version
   * 
   * Exemple:
   * @MethodName("test")
   * @Version("1.13")
   * myMethod() {}
   * 
   * AnnotationCaller.call("test", my_obj, null)
   * 
   * See: fr.epicanard.globalmarketchest.utils.Annotations.MethodName
   * See: fr.epicanard.globalmarketchest.utils.Annotations.Version
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
  public <T> T call(String methodName, Class<?> objectClass, Object object, Object ...args) throws MissingMethodException {
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