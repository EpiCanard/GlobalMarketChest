package fr.epicanard.globalmarketchest.exceptions;

import fr.epicanard.globalmarketchest.utils.WorldUtils;
import org.bukkit.Location;

public class ShopAlreadyExistException extends Exception {

  private static final long serialVersionUID = 1L;

  public ShopAlreadyExistException(Location loc) {
    super("A shop already exist at this position [" + WorldUtils.getStringFromLocation(loc) + "]");
  }
}
