package fr.epicanard.globalmarketchest.world_group;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.WorldAlreadyAdded;
import fr.epicanard.globalmarketchest.exceptions.WorldDoesntExist;

public class WorldGroupManager {
  YamlConfiguration config;
  Map<String, String> worlds = new HashMap<String, String>();

  public WorldGroupManager() {
    this.config = (YamlConfiguration) GlobalMarketChest.plugin.getConfigLoader().getWorldGroups();

    Set<String> worldGroups = this.config.getConfigurationSection("WorldGroups").getKeys(false);

    for (String worldGroup: worldGroups) {
      for(String world : this.config.getStringList("WorldGroups." + worldGroup + ".Worlds")) {
        try {
          if (Bukkit.getWorld(world) == null)
            throw new WorldDoesntExist(world);
          if (this.worlds.get(world) != null)
            throw new WorldAlreadyAdded(world, this.worlds.get(world));
          this.worlds.put(world, worldGroup);
        } catch(WorldDoesntExist | WorldAlreadyAdded e) {
          GlobalMarketChest.plugin.getLogger().log(Level.WARNING, e.getMessage());
        }
      }
    }
  }
  
  public String getWorldGroup(World world) {
    return this.worlds.get(world.getName());
  }
}
