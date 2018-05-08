package fr.epicanard.globalmarketchest.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.DatabaseException;

public class WorldUtils {
  public static Block getNearestMaterial(Location location, Material material)
  {
    int radius = 1;
    Block finalBlock = null;
    double distance = 6.0D;
    int diameter = 1 + 2 * radius;
    int x, y, z;

    for (int i = 0; i < Math.pow(diameter, 3); i++) {
      x = i % diameter - radius;
      y = i / (int)Math.pow(diameter, 2) - radius;
      z = (i % (int)Math.pow(diameter, 2)) / 3 - radius;
      
      if (x == 0 && y == 0 && z == 0)
        continue;
      
      Block tempBlock = location.getBlock().getRelative(x, y, z);
      if (tempBlock != null && tempBlock.getState().getType().compareTo(material) == 0) {
        double blockDistance = tempBlock.getLocation().distance(location);
        if (blockDistance < distance) {
          finalBlock = tempBlock;
          distance = blockDistance;
        }
      }
    }
    return finalBlock;
  }
  
  public static Location getLocationFromString(String locatString, Location location, String databaseVar) throws DatabaseException {
    String[] args = locatString.split(",");
    
    
    if (args.length != 4)
      return null;
    
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
    if (loc == null)
      return "";
    return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
  }

  public static Boolean compareLocations(Location first, Location second) {
    if (first == null || second == null)
      return false;
    if (first.getBlockX() == second.getBlockX() &&
      first.getBlockY() == second.getBlockY() &&
      first.getBlockZ() == second.getBlockZ() &&
      first.getWorld() == second.getWorld())
      return true;
    else
      return false;
  }
}
