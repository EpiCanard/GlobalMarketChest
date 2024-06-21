package fr.epicanard.globalmarketchest.exceptions;

import fr.epicanard.globalmarketchest.utils.Utils;

public class MissingMethodException extends Exception {

  static final long serialVersionUID = -7914157352976633988L;

  public MissingMethodException(String methodName) {
    super(String.format("Missing method %s for versions %s and latest", methodName, Utils.getVersion()));
  }
}
