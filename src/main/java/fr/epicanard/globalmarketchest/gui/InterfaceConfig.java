package fr.epicanard.globalmarketchest.gui;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.InvalidPaginatorParameter;
import fr.epicanard.globalmarketchest.gui.paginator.PaginatorConfig;
import fr.epicanard.globalmarketchest.gui.shops.toggler.TogglerConfig;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Getter
@Setter
public class InterfaceConfig {
  private ItemStack[] itemStacks = new ItemStack[54];
  private PaginatorConfig paginator;
  private List<TogglerConfig> togglers = new ArrayList<>();

  InterfaceConfig(ConfigurationSection config, String interfaceName) {
    this.loadPaginator(config, interfaceName, Collections.emptyList());
    this.loadTogglers(config, interfaceName, Collections.emptyList());
    this.loadInterface(config, interfaceName, Collections.emptyList());
  }

  InterfaceConfig(ConfigurationSection config, String interfaceName, List<InterfaceConfig> baseInterfaces) {
    this.loadPaginator(config, interfaceName, baseInterfaces);
    this.loadTogglers(config, interfaceName, baseInterfaces);
    this.loadInterface(config, interfaceName, baseInterfaces);
  }


  /**
   * Load paginator from config
   *
   * @param config         Base config section of interface
   * @param name           Name of interface
   * @param baseInterfaces List of base InterfaceConfig
   */
  private void loadPaginator(ConfigurationSection config, String name, List<InterfaceConfig> baseInterfaces) {

    ConfigurationSection sec = config.getConfigurationSection(name + ".Paginator");

    if (sec == null) {
      baseInterfaces.stream().map(InterfaceConfig::getPaginator).filter(Objects::nonNull)
          .findFirst().map(pag -> this.paginator = pag.duplicate());
      return;
    }
    try {
      this.paginator = new PaginatorConfig(
          sec.getInt("Height"),
          sec.getInt("Width"),
          sec.getInt("StartPos"),
          sec.getInt("PreviousPos", -1),
          sec.getInt("NextPos", -1),
          sec.getInt("NumPagePos", -1)
      );
    } catch (InvalidPaginatorParameter e) {
      GlobalMarketChest.plugin.getLogger().log(Level.WARNING, e.getMessage());
    }
  }

  /**
   * Load Togglers from config
   *
   * @param config         Base config section of interface
   * @param name           Name of interface
   * @param baseInterfaces List of base InterfaceConfig
   */
  private void loadTogglers(ConfigurationSection config, String name, List<InterfaceConfig> baseInterfaces) {
    this.togglers = baseInterfaces.stream().map(InterfaceConfig::getTogglers)
        .reduce(new ArrayList<>(), (acc, value) -> {
          acc.addAll(value);
          return acc;
        });

    List<Map<?, ?>> togglers = config.getMapList(name + ".Togglers");

    if (togglers.isEmpty())
      return;

    for (Map<?, ?> toggler : togglers) {
      TogglerConfig conf = new TogglerConfig(toggler);
      this.togglers.removeIf(e -> e.getPosition() == conf.getPosition());
      this.togglers.add(conf);
    }
  }

  /**
   * Load Interface from config
   *
   * @param config         Base config section of interface
   * @param name           Name of interface
   * @param baseInterfaces List of base InterfaceConfig
   */
  private void loadInterface(ConfigurationSection config, String name, List<InterfaceConfig> baseInterfaces) {
    this.itemStacks = baseInterfaces.stream().map(InterfaceConfig::getItemStacks)
        .reduce(new ItemStack[54], ItemStackUtils::mergeArray);

    ConfigurationSection itemsConfig = config.getConfigurationSection(name + ".Items");
    if (itemsConfig == null)
      return;

    Map<Integer, String> items = this.parseItems(itemsConfig.getValues(false));

    for (int i = 0; i < 54; i++) {
      ItemStack button = Utils.getButton(items.get(i));
      if (button != null)
        this.itemStacks[i] = button;
    }
  }

  /**
   * Parse items defined for the interface (ex: "2-5" to [2, 3, 4, 5] of the
   * current item) Generate map, to each position number assign an item
   *
   * @param its Map of interface items with group
   * @return Map of interface items without group
   */
  private Map<Integer, String> parseItems(Map<String, Object> its) {
    Map<Integer, String> items = new HashMap<>();

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
