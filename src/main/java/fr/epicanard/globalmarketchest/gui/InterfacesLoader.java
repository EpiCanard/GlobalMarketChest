package fr.epicanard.globalmarketchest.gui;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.paginator.PaginatorConfig;
import fr.epicanard.globalmarketchest.gui.shops.toggler.TogglerConfig;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;

/**
 * SINGLETON
 * Loader that load and store interfaces (pool of ItemStack) from the config
 * We can get the interface layout without having to check the configuration file each time
 */
public class InterfacesLoader {
  private static InterfacesLoader INSTANCE;
  private Map<String, InterfaceConfig> baseInterfaceConfigs = new HashMap<>();
  private Map<String, InterfaceConfig> interfaceConfigs = new HashMap<>();
  private Map<String, ItemStack> backgrounds = new HashMap<>();

  private InterfacesLoader() {
  }

  public static InterfacesLoader getInstance() {
    if (INSTANCE == null)
      INSTANCE = new InterfacesLoader();
    return INSTANCE;
  }

  /**
   * Get the interfaceConfig that match the interfaceName
   *
   * @param interfaceName Name of interface
   * @return Optional of InterfaceConfig
   */
  private Optional<InterfaceConfig> getInterfaceConfig(String interfaceName) {
    return Optional.ofNullable(this.interfaceConfigs.get(interfaceName));
  }

  /**
   * Get list of ItemStack for one interface
   *
   * @param interfaceName Name of interface
   * @return List of ItemStack
   */
  public ItemStack[] getInterface(final String interfaceName) {
    return this.getInterfaceConfig(interfaceName).map(InterfaceConfig::getItemStacks).orElse(null);
  }

  /**
   * Get PaginatorConfig and create duplicate
   *
   * @param interfaceName Name of interface
   * @return PaginatorConfig
   */
  public PaginatorConfig getPaginatorConfig(final String interfaceName) {
    return this.getInterfaceConfig(interfaceName).map(InterfaceConfig::getPaginator).map(PaginatorConfig::duplicate).orElse(null);
  }

  /**
   * Get togglers of one interface
   *
   * @param interfaceName Name of the interface
   * @return Togglers of interface sent in param
   */
  public List<TogglerConfig> getTogglers(final String interfaceName) {
    return this.getInterfaceConfig(interfaceName).map(InterfaceConfig::getTogglers).orElse(null);
  }

  /**
   * Get the background with his name
   *
   * @param backgroundName Name of background
   * @return Return background ItemStacl
   */
  public ItemStack getBackground(String backgroundName) {
    return this.backgrounds.getOrDefault(backgroundName, this.backgrounds.get("Default"));
  }

  /**
   * Load interfaces inside a map, where the key is the name of the interface
   * and the value a list of ItemStack
   * When there no item specified for a position it's filled with background item
   *
   * @param interfaceConfig YamlConfiguration
   * @param loadBase if set to true get base interfaces and it in final interface
   */
  private void loadInterfaces(ConfigurationSection interfaceConfig, Boolean loadBase) {
    Set<String> interfacesName = interfaceConfig.getKeys(false);

    for (String name : interfacesName) {
      if (loadBase) {
        final List<InterfaceConfig> baseConfigs = interfaceConfig.getStringList(name + ".Base").stream()
            .map(this.baseInterfaceConfigs::get).filter(Objects::nonNull).collect(Collectors.toList());
        this.interfaceConfigs.put(name, new InterfaceConfig(interfaceConfig, name, baseConfigs));
      } else {
        this.baseInterfaceConfigs.put(name, new InterfaceConfig(interfaceConfig, name, Collections.emptyList()));
      }
    }
  }

  /**
   * Load all interfaces in a map of string and itemstacks
   *
   * @param interfaceConfig YamlConfiguration that contains all interfaces
   */
  public void loadInterfaces(YamlConfiguration interfaceConfig) {
    this.interfaceConfigs.clear();
    this.baseInterfaceConfigs.clear();
    this.backgrounds.clear();

    this.loadBackgrounds();
    this.loadInterfaces(interfaceConfig.getConfigurationSection("BaseInterfaces"), false);
    this.loadInterfaces(interfaceConfig.getConfigurationSection("Interfaces"), true);
    this.interfaceConfigs.values().forEach(InterfaceConfig::fillBackground);
  }

  /**
   * Load backgrounds from config
   */
  private void loadBackgrounds() {
    ConfigurationSection section = GlobalMarketChest.plugin.getConfigLoader().getConfig().getConfigurationSection("Interfaces.Backgrounds");

    section.getValues(false).forEach((key, value) -> {
      final ItemStack itemStack = ItemStackUtils.getItemStack((String)value);
      this.backgrounds.put(key, ItemStackUtils.setItemStackMeta(itemStack, null, null));
    });

  }
}
