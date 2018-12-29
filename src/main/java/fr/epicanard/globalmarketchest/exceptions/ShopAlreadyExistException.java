package fr.epicanard.globalmarketchest.exceptions;

import org.bukkit.Location;

import fr.epicanard.globalmarketchest.utils.WorldUtils;

public class ShopAlreadyExistException extends Exception {

  private static final long serialVersionUID = 1L;

  public ShopAlreadyExistException(Location loc) {
    super("A shop already exist at this position [" + WorldUtils.getStringFromLocation(loc) + "]");
  }
}
