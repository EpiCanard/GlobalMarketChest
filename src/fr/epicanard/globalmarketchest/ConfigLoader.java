package fr.epicanard.globalmarketchest;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.epicanard.globalmarketchest.Main;

public class ConfigLoader {

  private FileConfiguration config, languages;
  private Main main;

  public ConfigLoader(Main m) {
    this.main = m;
  }

  public FileConfiguration getConfig() {
    return this.config;
  }

  public FileConfiguration getLanguages() {
    return this.languages;
  }

  private YamlConfiguration loadOneFile(String fileName) {
    if (!fileName.substring(fileName.length() - 4).equals(".yml")) {
      fileName += ".yml";
    }
    File confFile = new File(this.main.getDataFolder(), fileName);

    if (!confFile.exists()) {
      confFile.getParentFile().mkdirs();
      this.main.saveResource(fileName, false);
    }

    YamlConfiguration conf = new YamlConfiguration();
    try {
      conf.load(confFile);
      return conf;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvalidConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void loadFiles() {

    this.config = this.loadOneFile("config.yml");
    if (this.config != null) {
      String tmp = this.config.getString("General.lang");
      if (tmp == null) {
        tmp = "language.yml";
      }
      this.languages = this.loadOneFile(tmp);
    }

  }
}
