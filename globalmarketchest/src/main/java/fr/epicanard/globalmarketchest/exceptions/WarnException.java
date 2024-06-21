package fr.epicanard.globalmarketchest.exceptions;

public class WarnException extends Exception {

  private static final long serialVersionUID = 1L;

  public WarnException(String errorVar) {
    super(errorVar);
  }
}
