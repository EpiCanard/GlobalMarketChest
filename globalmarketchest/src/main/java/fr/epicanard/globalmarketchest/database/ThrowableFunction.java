package fr.epicanard.globalmarketchest.database;

@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Throwable> {

  /**
   * Function that take in parameter the type T return R
   * and can throw exception E
   * 
   * @param t Function parameter
   * @return Return type R
   * @throws E Can throw exception type E
   */
  R apply(T t) throws E;
}
