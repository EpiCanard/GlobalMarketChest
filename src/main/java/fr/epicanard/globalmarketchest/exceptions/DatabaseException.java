package fr.epicanard.globalmarketchest.exceptions;

public class DatabaseException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public DatabaseException(String message) {
    super("[Database] Some informations in database are wrong : " + message);
  }
}
