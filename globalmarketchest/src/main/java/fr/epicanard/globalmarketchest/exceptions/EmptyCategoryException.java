package fr.epicanard.globalmarketchest.exceptions;

public class EmptyCategoryException extends Exception {

  static final long serialVersionUID = -7914157672976633808L;

  public EmptyCategoryException(String message) {
    super(String.format("[Categories] Category '%s' has not items. Please add some items in '%s.Items' to the 'categories.yml' file", message, message));
  }
}
