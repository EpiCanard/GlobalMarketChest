package fr.epicanard.globalmarketchest.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.CantLoadConfigException;
import lombok.Getter;

public class ConfigLoader {

  @Getter
  private YamlConfiguration config;
  @Getter
  private YamlConfiguration languages;
  @Getter
  private YamlConfiguration categories;

  public ConfigLoader() {
    this.config = null;
    this.languages = null;
    this.categories =  null;
  }

  /**
   * Load one file from plugin folder and save it if it doesn't exist
   *
   * @throws CantLoadConfigException Throw this exception when the file can't be loaded (InvalidFile or wrong permissions)
   * @param filename Name of the file that must be load
   * @return Return the yamlconfiguration file
   */
  private YamlConfiguration loadOneFile(String fileName) throws CantLoadConfigException {
    if (!fileName.substring(fileName.length() - 4).equals(".yml"))
      fileName += ".yml";

    File confFile = new File(GlobalMarketChest.plugin.getDataFolder(), fileName);

    if (!confFile.exists()) {
      confFile.getParentFile().mkdirs();
      GlobalMarketChest.plugin.saveResource(fileName, false);
    }

    YamlConfiguration conf = new YamlConfiguration();
    try {
      conf.load(confFile);
      return conf;
    } catch (IOException | IllegalArgumentException | InvalidConfigurationException e) {
      throw new CantLoadConfigException(fileName);
    }
  }

  /**
   * Load one resource from jar. It doesn't extract it from the jar
   *
   * @param filename Name of the file that must be load
   * @return Return the yamlconfiguration file
   */
  public YamlConfiguration loadResource(String filename) {
    try {
      InputStream res = GlobalMarketChest.plugin.getResource(filename);
      return YamlConfiguration.loadConfiguration(new InputStreamReader(res));
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * Load all necessary resource files
   */
  public void loadFiles()  throws CantLoadConfigException {
    this.config = null;
    this.categories = null;
    this.languages = null;

    this.config = this.loadOneFile("config.yml");
    this.categories = this.loadOneFile("categories.yml");
    if (this.config != null) {
      String tmp = this.config.getString("General.Lang");
      if (tmp == null)
        tmp = "lang-en_EN.yml";
      this.languages = this.loadOneFile(tmp);
    }
  }
}
