package fr.epicanard.globalmarketchest.exceptions;

public class FailedInitException extends Exception {

  static final long serialVersionUID = -7914157672974233988L;

  public FailedInitException(String step) {
    super(String.format("Failed to init: %s", step));
  }
}
