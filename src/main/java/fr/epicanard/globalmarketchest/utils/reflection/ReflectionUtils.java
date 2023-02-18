package fr.epicanard.globalmarketchest.utils.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

  /**
   * Transform an object array into a class array
   *
   * @param args Object array
   * @return return an array of class from each object
   */
  public static Class<?>[] fromObjectToClass(Object[] args) {
    Class<?>[] classes = new Class<?>[0];

    if (args != null) {
      classes = new Class<?>[args.length];
      for (int i = 0; i < args.length; i++) {
        classes[i] = args[i].getClass();
      }
    }
    return classes;
  }

  /**
   * Invoke the method of an object
   *
   * @param object the object on which call the method
   * @param method the method name to calle
   * @param args all the arguments that must be send to the method
   * @return return the object return by the method
   */
  public static Object invokeMethod(Object object, String method, Object... args) {
    try {
      return object.getClass().getMethod(method, fromObjectToClass(args)).invoke(object, args);
    } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Invoke the method of an object
   *
   * @param object the object on which call the method
   * @param method the method name to
   * @param args all the arguments that must be send to the method
   * @return return the object return by the method
   */
  public static Object invokeMethod(Object object, Method method, Object... args) {
    try {
      return method.invoke(object, args);
    } catch (InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Define if the current object has the specified method
   *
   * @param object the object on which call the method
   * @param method the method name to calle
   * @param args all the arguments that must be send to the method
   * @return return a boolean
   */
  public static Boolean hasMethod(Object object, String method, Object... args) {
    try {
      object.getClass().getMethod(method, fromObjectToClass(args));
      return true;
    } catch (NoSuchMethodException e) {
      return false;
    }
  }

  /**
   * Create a new instance of given class
   *
   * @param clazz Class to instanciate
   * @param args Arguments sent to constructor
   * @return Return a new instance or null if constructor fail
   */
  public static Object newInstance(Class<?> clazz, Object... args) {
    try {
      return clazz.getConstructor(fromObjectToClass(args)).newInstance(args);
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  private ReflectionUtils() {}
}
