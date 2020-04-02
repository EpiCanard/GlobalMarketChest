package fr.epicanard.globalmarketchest.exceptions;

public class InterfaceLoadException extends RuntimeException {

  static final long serialVersionUID = -7914157672976642988L;

  public InterfaceLoadException(String message) {
    super(String.format("Can't load interface : %s", message));
  }
}
