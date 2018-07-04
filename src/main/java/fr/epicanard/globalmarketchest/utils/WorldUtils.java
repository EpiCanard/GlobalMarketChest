package fr.epicanard.globalmarketchest.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.google.common.util.concurrent.AtomicDouble;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.WorldDoesntExist;
import lombok.experimental.UtilityClass;

/**
 * Utility Class for world link action
 */
@UtilityClass
public class WorldUtils {

  /**
   * Get all the block in a specific radius around a location
   * 
   * @param location  Looking around this location
   * @param radius    Search radius
   * @param consumer  Consumer used with the specific block sent
   */
  private void getRadiusBlock(Location location, int radius, Consumer<Block> consumer) {
    int diameter = 1 + 2 * radius;
    int x, y, z;

    for (int i = 0; i < Math.pow(diameter, 3); i++) {
      x = i % diameter - radius;
      y = i / (int)Math.pow(diameter, 2) - radius;
      z = (i % (int)Math.pow(diameter, 2)) / 3 - radius;

      if (x == 0 && y == 0 && z == 0)
        continue;

      consumer.accept(location.getBlock().getRelative(x, y, z));
    }
  }

  /**
   * Get the nearest block that match with the material in parameter
   * 
   * @param location  Looking around this location
   * @param material  Material to search
   */
  public Block getNearestMaterial(Location location, Material material)
  {
    AtomicDouble distance = new AtomicDouble(6.0D);
    AtomicReference<Block> finalBlock = new AtomicReference<Block>();

    WorldUtils.getRadiusBlock(location, 1, block -> {
      if (block != null && block.getState().getType().compareTo(material) == 0) {
        double blockDistance = block.getLocation().distance(location);
        if (blockDistance < distance.get()) {
          finalBlock.set(block);
          distance.set(blockDistance);
        }
      }
    });
    return finalBlock.get();
  }

  /**
   * Get all the block inside the radius that match with the allowed block specified in config
   * 
   * @param location Looking around this location
   * @return Return the list of block that match
   */
  public List<Block> getNearAllowedBlocks(Location location) {
    List<Block> listBlocks = new ArrayList<Block>();
    List<Material> allowed = ShopUtils.getAllowedLinkBlock();

    WorldUtils.getRadiusBlock(location, 1, block -> {
      if (allowed.contains(block.getType()))
        listBlocks.add(block);
    });
    return listBlocks;
  }

  /**
   * Transform a String into a location
   * 
   * @param locatString String location
   * @param location    if not null set location inside this variable
   * @return Location
   */
  public Location getLocationFromString(String locatString, Location location) {
    if (locatString == null)
      return null;
    String[] args = locatString.split(",");

    if (args.length != 4)
      return null;
    
    if (location == null)
      location = new Location(GlobalMarketChest.plugin.getServer().getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
    else {
      World world = GlobalMarketChest.plugin.getServer().getWorld(args[0]);
      if (world == null) {
        LoggerUtils.warn(LangUtils.get("ErrorMessages.UnkownWorld") + " " + locatString);
        return null;
      }
      location.setWorld(world);
      location.setX(Double.parseDouble(args[1]));
      location.setY(Double.parseDouble(args[2]));
      location.setZ(Double.parseDouble(args[3]));
    }
    return location;
  }
  
  /**
   * Transform a Location in a String
   * 
   * @param loc  Location to transform
   */
  public String getStringFromLocation(Location loc) {
    if (loc == null)
      return "";
    return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
  }

  /**
   * Compare 2 locations
   * 
   * @param first
   * @param second
   * @return Return true if they are equals
   */
  public Boolean compareLocations(Location first, Location second) {
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

  /**
   * Verify if the specified World is allowed to create shop
   * 
   * @throws WorldDoesnExist
   * @return false if it not allowed
   */
  public Boolean isAllowedWorld(String worldName) throws WorldDoesntExist {
    if (Bukkit.getWorld(worldName) == null)
      throw new WorldDoesntExist(worldName);
    return GlobalMarketChest.plugin.getConfigLoader().getConfig().getList("WorldAllowed").contains(worldName);
  }
}
