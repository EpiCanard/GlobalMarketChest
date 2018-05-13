package fr.epicanard.globalmarketchest.exceptions;

public class TypeNotSupported extends Exception {

  static final long serialVersionUID = -7914157672976633815L;

  public TypeNotSupported(String type) {
    super("[QueryBuilder] Type '" + type + "' not supported" );
  }
}
