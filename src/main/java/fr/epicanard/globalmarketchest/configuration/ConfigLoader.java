package fr.epicanard.globalmarketchest.configuration;

import fr.epicanard.duckconfig.DuckLoader;
import fr.epicanard.duckconfig.annotations.ResourceWrapper;
import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.CantLoadConfigException;
import fr.epicanard.globalmarketchest.utils.ConfigUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    this.categories = null;
  }

  /**
   * Save an InputStream inside a file already opened
   *
   * @param file   File opened
   * @param stream Stream to right inside
   * @throws IOException
   */
  private void saveStream(File file, InputStream stream) throws IOException {
    OutputStream out = new FileOutputStream(file);
    byte[] buf = new byte[1024];
    int len;

    while ((len = stream.read(buf)) > 0) {
      out.write(buf, 0, len);
    }
    out.close();
    stream.close();
  }

  /**
   * Load one file from plugin folder and save it if it doesn't exist
   *
   * @param fileName Name of the file that must be load
   * @param path     Path to get the file inside jar
   * @return Return the yamlconfiguration file
   * @throws CantLoadConfigException Throw this exception when the file can't be loaded (InvalidFile or wrong permissions)
   */
  private YamlConfiguration loadOneFile(String fileName, String path, String alternatePath) throws CantLoadConfigException {
    if (!fileName.substring(fileName.length() - 4).equals(".yml"))
      fileName += ".yml";
    final String finalFileName = fileName;

    final File confFile = new File(GlobalMarketChest.plugin.getDataFolder(), fileName);
    final YamlConfiguration conf = new YamlConfiguration();

    try {
      if (!confFile.exists()) {
        confFile.getParentFile().mkdirs();
        final UnaryOperator<String> processPath = (p) -> (p != null ? p + "/" : "") + finalFileName;
        InputStream stream = GlobalMarketChest.plugin.getResource(processPath.apply(path));
        if (stream == null) {
          if (alternatePath != null) {
            stream = GlobalMarketChest.plugin.getResource(processPath.apply(alternatePath));
          } else {
            throw new CantLoadConfigException(fileName);
          }
        }

        this.saveStream(confFile, stream);
      }

      conf.load(confFile);
      return conf;
    } catch (IOException | IllegalArgumentException | InvalidConfigurationException e) {
      throw new CantLoadConfigException(fileName);
    }
  }

  /**
   * Load one file from plugin folder and save it if it doesn't exist
   *
   * @param fileName Name of the file that must be load
   * @return Return the yamlconfiguration file
   * @throws CantLoadConfigException Throw this exception when the file can't be loaded (InvalidFile or wrong permissions)
   */
  private YamlConfiguration loadOneFile(String fileName) throws CantLoadConfigException {
    return this.loadOneFile(fileName, null, null);
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
  public void loadFiles() throws CantLoadConfigException {
    this.config = null;
    this.categories = null;
    this.languages = null;

    this.config = this.loadOneFile("config.yml", Utils.getVersion(), Utils.getLastSupportedVersion());
    this.categories = this.loadOneFile("categories.yml", Utils.getVersion(), Utils.getLastSupportedVersion());
    if (this.config != null) {
      final String langFilename = this.config.getString("General.Lang", "lang-en_US.yml");
      this.languages = this.loadOneFile(langFilename, "langs", null);
    }
  }

  /**
   * Load the price limits config file and save item just after
   *
   * @return Price limit for each item configured
   */
  public Map<String, PriceLimit> loadPriceLimitConfig() {
    if (!ConfigUtils.getBoolean("Price.LimitPriceChoice", false)) {
      return null;
    }

    final ResourceWrapper resource = new ResourceWrapper(GlobalMarketChest.plugin.getDataFolder().getPath(), "price-limits.yml");
    Map<String, PriceLimit> prices = DuckLoader.loadMap(PriceLimit.class, resource).entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> entry.getValue().checkValidity()
        ));

    if (prices.size() == 0) {
      prices = this.getMaterials().collect(Collectors.toMap(mat -> mat, mat -> new PriceLimit()));
    }
    DuckLoader.save(prices, resource);
    return prices;
  }

  /**
   * Get list the minecraft keys of materials of current minecraft version
   * Ignore Blacklisted items
   *
   * @return List of minecraft keys
   */
  private Stream<String> getMaterials() {
    final List<String> excludedItems = ConfigUtils.getStringList("ItemsBlacklist.Items");

    return Arrays.stream(Material.values())
        .map(mat -> {
          ItemStack item = new ItemStack(mat);
          return ItemStackUtils.getMinecraftKey(item);
        })
        .filter(mat -> !mat.equals("minecraft:air") && !excludedItems.contains(mat));
  }
}
