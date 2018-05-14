package fr.epicanard.globalmarketchest.exceptions;

public class WorldDoesntExist extends Exception {

  static final long serialVersionUID = -7914157672976673815L;

  public WorldDoesntExist(String worldName) {
    super("[WorldGroup] WorldName '" + worldName + "' doesn't exist, world ignored" );
  }
}
