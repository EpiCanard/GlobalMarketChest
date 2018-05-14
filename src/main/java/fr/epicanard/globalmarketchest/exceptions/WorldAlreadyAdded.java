package fr.epicanard.globalmarketchest.exceptions;

public class WorldAlreadyAdded extends Exception {

  static final long serialVersionUID = -7914857672976673815L;

  public WorldAlreadyAdded(String worldName, String worldGroup) {
    super("[WorldGroup] WorldName '" + worldName + "' is already added in worldgroup '" + worldGroup + "', can't be present in twice, world ignored" );
  }
}
