package fr.epicanard.globalmarketchest.utils;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

/**
 * Utility Class to get config parameters
 */
@UtilityClass
public class ConfigUtils {
  public YamlConfiguration get() {
    return GlobalMarketChest.plugin.getConfigLoader().getConfig();
  }

  public String getString(final String path, final String defaultValue) {
    return GlobalMarketChest.plugin.getConfigLoader().getConfig().getString(path, defaultValue);
  }

  public String getString(final String path) {
    return GlobalMarketChest.plugin.getConfigLoader().getConfig().getString(path);
  }

  public boolean getBoolean(final String path, final boolean defaultValue) {
    return GlobalMarketChest.plugin.getConfigLoader().getConfig().getBoolean(path, defaultValue);
  }

  public boolean getBoolean(final String path) {
    return GlobalMarketChest.plugin.getConfigLoader().getConfig().getBoolean(path);
  }

  public int getInt(final String path, final int defaultValue) {
    return GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt(path, defaultValue);
  }

  public int getInt(final String path) {
    return GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt(path);
  }

  public double getDouble(final String path, final double defaultValue) {
    return GlobalMarketChest.plugin.getConfigLoader().getConfig().getDouble(path, defaultValue);
  }

  public double getDouble(final String path) {
    return GlobalMarketChest.plugin.getConfigLoader().getConfig().getDouble(path);
  }

  public List<String> getStringList(final String path) {
    return GlobalMarketChest.plugin.getConfigLoader().getConfig().getStringList(path);
  }
}

