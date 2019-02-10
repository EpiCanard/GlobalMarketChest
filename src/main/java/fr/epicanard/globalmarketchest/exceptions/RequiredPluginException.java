package fr.epicanard.globalmarketchest.exceptions;

public class RequiredPluginException extends Exception {

  private static final long serialVersionUID = 1L;

  public RequiredPluginException(String name) {
    super("[PluginRequired] " + name + " plugin is missing or not enabled");
  }
}
