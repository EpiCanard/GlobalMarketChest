package fr.epicanard.globalmarketchest.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.InvalidPaginatorParameter;
import fr.epicanard.globalmarketchest.gui.paginator.PaginatorConfig;
import fr.epicanard.globalmarketchest.utils.Utils;

/**
 * SINGLETON
 * Loader that load and store interfaces (pool of ItemStack) from the config
 * We can get the interface layout without having to check the configuration file each time
 */
public class InterfacesLoader {
  private static InterfacesLoader INSTANCE;
  private Map<String, ItemStack[]> interfaces;
  private Map<String, PaginatorConfig> paginators;

  private InterfacesLoader() {
  }

  public static InterfacesLoader getInstance() {
    if (INSTANCE == null)
      INSTANCE = new InterfacesLoader();
    return INSTANCE;
  }

  /**
   * Get all interfaces loaded
   * 
   * @return
   */
  public Map<String, ItemStack[]> getInterfaces() {
    return this.interfaces;
  }

  /**
   * Get list of ItemStack for one interface
   * 
   * @param interfaceName
   * @return
   */
  public ItemStack[] getInterface(String interfaceName) {
    return (this.interfaces != null) ? this.interfaces.get(interfaceName) : null;
  }

  /**
   * Get PaginatorConfig and create duplicate
   * @param interfaceName
   * @return PaginatorConfig
   */
  public PaginatorConfig getPaginatorConfig(String interfaceName) {
    return this.paginators.get(interfaceName).duplicate();
  }

  /**
   * Load Paginator from config if exist and add it inside paginators map
   * @param config
   * @param name
   */
  private void loadPaginator(YamlConfiguration config, String name) {
    ConfigurationSection sec = config.getConfigurationSection(name + ".Paginator");

    if (sec == null)
      return;
    try {
      this.paginators.put(name, new PaginatorConfig(
        sec.getInt("Height"),
        sec.getInt("Width"),
        sec.getInt("StartPos"),
        sec.getInt("PreviousPos", -1),
        sec.getInt("NextPos", -1),
        sec.getInt("NumPagePos", -1)
        ));
    } catch (InvalidPaginatorParameter e) {
      GlobalMarketChest.plugin.getLogger().log(Level.WARNING, e.getMessage());
    }
  }

  /**
   * Load Interface from config if add it inside interfaces map
   * @param config
   * @param name
   */
  private void loadInterface(YamlConfiguration config, String name) {
    ItemStack[] itemsStack = new ItemStack[54];
    Map<Integer, String> items = this.parseItems(config.getConfigurationSection(name + ".Items").getValues(false));

    for (int i = 0; i < 54; i++)
      itemsStack[i] = Utils.getButton(items.get(i));
    this.interfaces.put(name, itemsStack);
  }

  /**
   * Load interfaces Create a map, where the key is the name of the interface
   * and the value a list of ItemStack
   * When there no item specified for a position it's filled with background item
   * 
   * @param interfaceConfig YamlConfiguration
   * @param reload if it's set to true force reload from configuraiton
   * @return
   */
  public Map<String, ItemStack[]> loadInterfaces(YamlConfiguration interfaceConfig, Boolean reload) {
    if (reload == false && this.interfaces != null)
      return this.interfaces;

    Set<String> interfacesName = interfaceConfig.getKeys(false);
    this.interfaces = new HashMap<String, ItemStack[]>();
    this.paginators = new HashMap<String, PaginatorConfig>();

    for (String name : interfacesName) {
      this.loadInterface(interfaceConfig, name);
      this.loadPaginator(interfaceConfig, name);
    }
    return this.interfaces;
  }

  /**
   * Overload "loadInterfaces" method to set param reload default to false
   * 
   * @param interfaceConfig
   * @return
   */
  public Map<String, ItemStack[]> loadInterfaces(YamlConfiguration interfaceConfig) {
    return this.loadInterfaces(interfaceConfig, false);
  }

  /**
   * Parse items defined for the interface (ex: "2-5" to [2, 3, 4, 5] of the
   * current item) Generate map, to each position number assign an item
   * 
   * @param its
   * @return
   */
  private Map<Integer, String> parseItems(Map<String, Object> its) {
    Map<Integer, String> items = new HashMap<Integer, String>();

    its.forEach((key, value) -> {
      try {
        items.put(Integer.parseInt(key), (String) value);
      } catch (NumberFormatException e) {
        String[] nums = key.split("-");
        for (Integer i = Integer.parseInt(nums[0]); i <= Integer.parseInt(nums[1]); i++)
          items.put(i, (String) value);
      }
    });
    return items;
  }

}
