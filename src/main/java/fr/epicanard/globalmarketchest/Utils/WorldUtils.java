package fr.epicanard.globalmarketchest.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.Exceptions.DatabaseException;

public class WorldUtils {
  public static Block getNearestMaterial(Location location, Material material)
  {    
    int radius = 1;
    Block finalBlock = null;
    double distance = 6.0D;

    for (int x = -radius; x <= radius; x++) {
      for (int y = -radius; y <= radius; y++) {
        for (int z = -radius; z <= radius; z++) {
          if ((x != 0) || (y != 0) || (z != 0)) {
            Block tempBlock = location.getBlock().getRelative(x, y, z);
            if (tempBlock != null && tempBlock.getState().getType().compareTo(material) == 0) {
              double blockDistance = tempBlock.getLocation().distance(location);
              if (blockDistance < distance) {
                finalBlock = tempBlock;
                distance = blockDistance;
              }
            }
          }
        }
      }
    }
    return finalBlock;
  }
  
  public static Location getLocationFromString(String locatString, Location location, String databaseVar) throws DatabaseException {
    String[] args = locatString.split(",");
    
    
    if (args.length != 4)
      throw new DatabaseException((databaseVar == null) ? "signLocation" : databaseVar);
    
    if (location == null)
      location = new Location(GlobalMarketChest.plugin.getServer().getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
    else {
      location.setWorld(GlobalMarketChest.plugin.getServer().getWorld(args[0]));
      location.setX(Double.parseDouble(args[1]));
      location.setY(Double.parseDouble(args[2]));
      location.setZ(Double.parseDouble(args[3]));
    }
    return location;
  }
  
  public static String getStringFromLocation(Location loc) {
    return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
  }

  public static Boolean compareLocations(Location first, Location second) {
    if (first.getBlockX() == second.getBlockX() &&
      first.getBlockY() == second.getBlockY() &&
      first.getBlockZ() == second.getBlockZ() &&
      first.getWorld() == second.getWorld())
      return true;
    else
      return false;
  }
}
