package fr.epicanard.globalmarketchest.exceptions;

public class InvalidPaginatorParameter extends Exception {

  static final long serialVersionUID = -7914157672979933815L;

  public InvalidPaginatorParameter(String type) {
    super("[Paginator] Invalid parameter '" + type + "'");
  }
}
