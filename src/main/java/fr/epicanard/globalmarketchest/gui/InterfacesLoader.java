package fr.epicanard.globalmarketchest.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.InvalidPaginatorParameter;
import fr.epicanard.globalmarketchest.gui.paginator.PaginatorConfig;
import fr.epicanard.globalmarketchest.gui.shops.toggler.TogglerConfig;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

/**
 * SINGLETON
 * Loader that load and store interfaces (pool of ItemStack) from the config
 * We can get the interface layout without having to check the configuration file each time
 */
public class InterfacesLoader {
  private static InterfacesLoader INSTANCE;
  private Map<String, ItemStack[]> interfaces = new HashMap<>();
  private Map<String, PaginatorConfig> paginators = new HashMap<>();
  private Map<String, ItemStack[]> baseInterfaces = new HashMap<>();
  private Map<String, PaginatorConfig> basePaginators = new HashMap<>();
  private Map<String, List<TogglerConfig>> togglers = new HashMap<>();
  private Map<String, List<TogglerConfig>> baseTogglers = new HashMap<>();

  private InterfacesLoader() {
  }

  public static InterfacesLoader getInstance() {
    if (INSTANCE == null)
      INSTANCE = new InterfacesLoader();
    return INSTANCE;
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
    PaginatorConfig conf = this.paginators.get(interfaceName);
    return (conf != null) ? conf.duplicate() : null;
  }

  /**
   * Get togglers of one interface
   *
   * @param interfaceName Name of the interface
   * @return Togglers of interface sent in param
   */
  public List<TogglerConfig> getTogglers(String interfaceName) {
    return this.togglers.get(interfaceName);
  }

  /**
   * Load Circle Togle from config if exist and add it inside circleToggler map
   * @param config
   * @param name
   */
  private void loadTogglers(ConfigurationSection config, String name, Map<String, List<TogglerConfig>> map) {
    List<Map<?, ?>> togglers = config.getMapList(name + ".Togglers");

    if (togglers.isEmpty())
      return;
    List<TogglerConfig> togglerList = Utils.getOrElse(map.get(name), new ArrayList<>());
    for (Map<?, ?> toggler : togglers) {
      TogglerConfig conf = new TogglerConfig(toggler);
      togglerList.removeIf(e -> e.getPosition() == conf.getPosition());
      togglerList.add(conf);
    }
    if (!togglerList.isEmpty())
      map.put(name, togglerList); 
  }

  /**
   * Load Paginator from config if exist and add it inside paginators map
   * @param config
   * @param name
   */
  private void loadPaginator(ConfigurationSection config, String name, Map<String, PaginatorConfig> map) {
    ConfigurationSection sec = config.getConfigurationSection(name + ".Paginator");

    if (sec == null)
      return;
    try {
      map.put(name, new PaginatorConfig(
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
  private void loadInterface(ConfigurationSection config, String name, Map<String, ItemStack[]> map) {
    ConfigurationSection itemsConfig = config.getConfigurationSection(name + ".Items");
    if (itemsConfig == null)
      return;
    Map<Integer, String> items = this.parseItems(itemsConfig.getValues(false));

    if (map.get(name) == null) {
      ItemStack[] itemsStack = new ItemStack[54];
      for (int i = 0; i < 54; i++)
        itemsStack[i] = Utils.getButton(items.get(i));
        map.put(name, itemsStack);
      } else {
      for (Integer key : items.keySet())
        map.get(name)[key] = Utils.getButton(items.get(key));
    }
  }

  /**
   * Load interfaces inside a map, where the key is the name of the interface
   * and the value a list of ItemStack
   * When there no item specified for a position it's filled with background item
   *
   * @param interfaceConfig YamlConfiguration
   * @param loadBase if set to true get base interfaces and it in final interface
   * @return
   */
  private void loadInterfaces(ConfigurationSection interfaceConfig, Boolean loadBase) {
    Set<String> interfacesName = interfaceConfig.getKeys(false);

    for (String name : interfacesName) {
      if (loadBase) {
        for (String base : interfaceConfig.getStringList(name + ".Base")) {
          Optional.ofNullable(this.baseInterfaces.get(base)).ifPresent(i -> {
            if (this.interfaces.get(name) == null)
              this.interfaces.put(name, Arrays.copyOf(i, i.length));
            else
              ItemStackUtils.mergeArray(this.interfaces.get(name), i);
          });
          Optional.ofNullable(this.basePaginators.get(base)).ifPresent(p -> this.paginators.put(name, p.duplicate()));
          Optional.ofNullable(this.baseTogglers.get(base)).ifPresent(c -> this.togglers.put(name, new ArrayList<TogglerConfig>(c)));
        }
      }
      this.loadInterface(interfaceConfig, name, (loadBase) ? this.interfaces : this.baseInterfaces);
      this.loadPaginator(interfaceConfig, name, (loadBase) ? this.paginators : this.basePaginators);
      this.loadTogglers(interfaceConfig, name, (loadBase) ? this.togglers : this.baseTogglers);
    }
  }

  /**
   * Load all interfaces in a map of string and itemstacks
   *
   * @param interfaceConfig YamlConfiguration
   * @return
   */
  public Map<String, ItemStack[]> loadInterfaces(YamlConfiguration interfaceConfig) {
    this.interfaces.clear();
    this.paginators.clear();
    this.baseInterfaces.clear();
    this.basePaginators.clear();

    this.loadInterfaces(interfaceConfig.getConfigurationSection("BaseInterfaces"), false);
    this.loadInterfaces(interfaceConfig.getConfigurationSection("Interfaces"), true);
    return this.interfaces;
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
