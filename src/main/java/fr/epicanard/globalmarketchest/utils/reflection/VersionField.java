package fr.epicanard.globalmarketchest.utils.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * VersionField
 *
 * Reflection class that contains an object value
 * Allow to chain the call
 *
 * Vanilla Java
 * myObject.afield.anotherfield
 *
 * Reflection VersionField
 * VersionField.from(myObject).get("afield").get("anotherfield").value()
 */
class VersionField {

  /**
   * Field value
   */
  private Object object;

  /**
   * The constructor
   *
   * @param obj value to assign to object
   */
  VersionField(Object obj) {
    this.object = obj;
  }

  /**
   * Create classe instance
   */
  public static VersionField from(Object obj) {
    return new VersionField(obj);
  }

  /**
   * Reflection method to get a field value
   * Store the field value and return a new instance of VersionField
   *
   * @param name field name
   * @return a new instance of VersionField
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   */
  VersionField get(String name) throws NoSuchFieldException, IllegalAccessException {
    Object ret = this.object.getClass().getField(name).get(this.object);
    return new VersionField(ret);
  }

  /**
   * Reflection method to get a field with type
   * Store the field value and return a new instance of VersionField
   *
   * @param fieldType field type
   * @return a new instance of VersionField
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   */
  VersionField getWithType(Class<?> fieldType) throws NoSuchFieldException, IllegalAccessException {
    final Optional<Field> maybeField = Arrays.stream(this.object.getClass().getFields())
            .filter(f -> f.getType().isAssignableFrom(fieldType)).findFirst();
    if (!maybeField.isPresent()) {
      throw new NoSuchFieldException("Can't find field with type : " + fieldType.getName());
    }
    return new VersionField(maybeField.get().get(this.object));
  }

  /**
   * Reflection method to invoke a method with type
   *
   * @param returnType return type
   * @return a new instance of VersionField
   * @throws NoSuchMethodException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  Object invokeMethodWithType(Class<?> returnType) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    final Optional<Method> maybeMethod = Arrays.stream(this.object.getClass().getMethods())
            .filter(m -> m.getReturnType().isAssignableFrom(returnType)).findFirst();
    if (!maybeMethod.isPresent()) {
      throw new NoSuchMethodException("Can't find methode with return type : " + returnType.getName());
    }
    return maybeMethod.get().invoke(this.object);
  }

  /**
   * Return the object value stored
   *
   * @return
   */
  Object value() {
    return this.object;
  }
}
