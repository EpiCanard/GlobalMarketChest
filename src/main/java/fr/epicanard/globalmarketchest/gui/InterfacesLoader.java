package fr.epicanard.globalmarketchest.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.InvalidPaginatorParameter;
import fr.epicanard.globalmarketchest.gui.paginator.PaginatorConfig;
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
  private Map<String, List<Pair<Integer, Boolean>>> circleTogglers = new HashMap<>();
  private Map<String, List<Pair<Integer, Boolean>>> baseCircleTogglers = new HashMap<>();

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

  public List<Pair<Integer, Boolean>> getCircleTogglers(String interfaceName) {
    return this.circleTogglers.get(interfaceName);
  }
  /**
   * Load Circle Togle from config if exist and add it inside circleToggler map
   * @param config
   * @param name
   */
  private void loadCircleToggler(ConfigurationSection config, String name, Map<String, List<Pair<Integer, Boolean>>> map) {
    List<Map<?, ?>> circles = config.getMapList(name + ".CircleToggler");

    if (circles.isEmpty())
      return;
    List<Pair<Integer, Boolean>> circleList = new ArrayList<>();
    for (Map<?, ?> circle : circles) {
      Integer pos = (Integer)circle.get("Pos");
      if (pos == null)
        continue;
      Boolean set = (Boolean)circle.get("Set");
      if (set == null)
        set = false;
      circleList.add(new ImmutablePair<Integer,Boolean>(pos, set));
    }
    if (!circleList.isEmpty())
      map.put(name, circleList);
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
          Optional.ofNullable(this.baseCircleTogglers.get(base)).ifPresent(c -> this.circleTogglers.put(name, c));
        }
      }
      this.loadInterface(interfaceConfig, name, (loadBase) ? this.interfaces : this.baseInterfaces);
      this.loadPaginator(interfaceConfig, name, (loadBase) ? this.paginators : this.basePaginators);
      this.loadCircleToggler(interfaceConfig, name, (loadBase) ? this.circleTogglers : this.baseCircleTogglers);
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
