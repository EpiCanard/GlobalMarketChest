package fr.epicanard.globalmarketchest.exceptions;

public class CantLoadConfigException extends Exception {

  static final long serialVersionUID = -7914157672976633988L;

  public CantLoadConfigException(String file) {
    super(String.format("Can't load the file `%s`, verify if it is present, valid and with correct access right", file));
  }
}
