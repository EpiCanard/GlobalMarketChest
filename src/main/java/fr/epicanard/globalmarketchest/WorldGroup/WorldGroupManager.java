package fr.epicanard.globalmarketchest.WorldGroup;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.epicanard.globalmarketchest.GlobalMarketChest;

public class WorldGroupManager {
  YamlConfiguration config;
  
  public WorldGroupManager() {
    System.out.println(GlobalMarketChest.plugin);
    this.config = (YamlConfiguration) GlobalMarketChest.plugin.getConfigLoader().getWorldGroups();
  }
  
  public String getWorldGroup(World world) {
    return "MyWorld";
  }
}
