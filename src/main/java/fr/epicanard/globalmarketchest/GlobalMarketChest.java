package fr.epicanard.globalmarketchest;

import fr.epicanard.globalmarketchest.commands.CommandHandler;
import fr.epicanard.globalmarketchest.configuration.ConfigLoader;
import fr.epicanard.globalmarketchest.database.PatchHandler;
import fr.epicanard.globalmarketchest.database.connectors.DatabaseConnector;
import fr.epicanard.globalmarketchest.database.connectors.MySQLConnector;
import fr.epicanard.globalmarketchest.database.connectors.SQLiteConnector;
import fr.epicanard.globalmarketchest.economy.VaultEconomy;
import fr.epicanard.globalmarketchest.exceptions.CantLoadConfigException;
import fr.epicanard.globalmarketchest.exceptions.ConfigException;
import fr.epicanard.globalmarketchest.exceptions.FailedInitException;
import fr.epicanard.globalmarketchest.exceptions.RequiredPluginException;
import fr.epicanard.globalmarketchest.gui.CategoryHandler;
import fr.epicanard.globalmarketchest.gui.InterfacesLoader;
import fr.epicanard.globalmarketchest.gui.InventoriesHandler;
import fr.epicanard.globalmarketchest.listeners.*;
import fr.epicanard.globalmarketchest.managers.AuctionManager;
import fr.epicanard.globalmarketchest.managers.ShopManager;
import fr.epicanard.globalmarketchest.ranks.RanksLoader;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.logging.Level;


public class GlobalMarketChest extends JavaPlugin {

  @Getter
  private final ConfigLoader configLoader;
  @Getter
  private final RanksLoader ranksLoader;
  @Getter
  private DatabaseConnector sqlConnector;
  public static GlobalMarketChest plugin;
  public final InventoriesHandler inventories;
  public final VaultEconomy economy;
  public final ShopManager shopManager;
  public final AuctionManager auctionManager;
  @Getter
  private CategoryHandler catHandler;

  public GlobalMarketChest() {
    // Initialization of loader
    this.configLoader = new ConfigLoader();
    this.inventories = new InventoriesHandler();
    this.economy = new VaultEconomy();
    this.shopManager = new ShopManager();
    this.auctionManager = new AuctionManager();
    this.ranksLoader = new RanksLoader();
  }

  @Override
  public void onEnable() {
    GlobalMarketChest.plugin = this;

    try {
      this.configLoader.loadFiles();
    } catch (CantLoadConfigException e) {
      this.getLogger().log(Level.SEVERE, e.getMessage());
      this.disable();
      return;
    }

    this.catHandler = new CategoryHandler(GlobalMarketChest.plugin.getConfigLoader().getCategories());
    ShopUtils.init();

    YamlConfiguration defConfig = this.configLoader.loadResource("interfaces.yml");
    InterfacesLoader.getInstance().loadInterfaces(defConfig);

    try {
      this.initEconomy();
      this.initDatabase();
    } catch (FailedInitException e) {
      LoggerUtils.warn(e.getMessage());
      this.disable();
      return;
    } catch (Exception e) {
      e.printStackTrace();
      this.disable();
      return;
    }

    this.ranksLoader.loadRanks();
    this.shopManager.loadShops();

    getCommand("GlobalMarketChest").setExecutor(new CommandHandler());

    this.register(new ChatListener());
    this.register(new GUIListener());
    this.register(new CloseGUICollector());
    this.register(new WorldListener());
    this.register(new ShopCreationListener());
    this.register(new PlayerListener());
    final Listener moneyExchange = getPlugin("MysqlPlayerDataBridge")
        .filter(DataBridgeListener::canBeEnabled)
        .map(dataBridge -> (Listener)new DataBridgeListener(dataBridge))
        .orElseGet(MoneyExchangeListener::new);
    this.register(moneyExchange);
  }

  @Override
  public void onDisable() {
    if (this.sqlConnector != null)
      this.sqlConnector.cleanPool();
    if (this.inventories != null)
      this.inventories.removeAllInventories();
    HandlerList.unregisterAll(this);
  }

  /**
   * Disable the plugin
   */
  private void disable() {
    this.setEnabled(false);
    this.getLogger().log(Level.WARNING, "Plugin GlobalMarketChest disabled");
  }

  /**
   * Reload the plugin and send message to player
   *
   * @param sender Sender to send messages
   */
  public void reload(CommandSender sender) {
    PlayerUtils.sendMessage(sender, LangUtils.get("InfoMessages.PluginReloading"));
    this.setEnabled(false);
    this.setEnabled(true);
    PlayerUtils.sendMessage(sender, LangUtils.get("InfoMessages.PluginReloaded"));
  }

  /**
   * Init the economy plugin
   *
   * @throws FailedInitException Throw exception when fail to init
   */
  private void initEconomy() throws FailedInitException {
    try {
      this.economy.initEconomy();
    } catch (RequiredPluginException e) {
      this.getLogger().log(Level.WARNING, e.getMessage());
      throw new FailedInitException("Economy");
    }
  }

  /**
   * Init database create database connection and configure it
   *
   * @throws FailedInitException Throw exception when fail to init
   */
  private void initDatabase() throws FailedInitException {
    try {
      this.sqlConnector = this.getDatabaseConnectionProvider();
      if (this.sqlConnector.needConnection) {
        this.sqlConnector.configFromConfigFile();
      }
      this.sqlConnector.fillPool();
      DatabaseConnector.configureTables();
      new PatchHandler(this.sqlConnector).applyPatches();
    } catch (ConfigException e) {
      this.getLogger().log(Level.WARNING, "[SQLConnection] " + e.getMessage());
      throw new FailedInitException("Database");
    }
  }

  /**
   * Init Database connection provider depending of config 'Storage.Type'
   *
   * @return Return the correct Database connection provider
   * @throws ConfigException Throw ConfigException when connectType doesn't exist
   */
  private DatabaseConnector getDatabaseConnectionProvider() throws ConfigException {
    final String connectionType = this.configLoader.getConfig().getString("Storage.Type");
    switch (connectionType) {
      case "sqlite":
        return new SQLiteConnector();
      case "mysql":
        return new MySQLConnector();
      default:
        throw new ConfigException("Wrong value of 'Storage.Type'. \nWanted: sqlite or mysql.\nBut get: " + connectionType);
    }
  }

  /**
   * Register a listener
   *
   * @param listener Listener to register
   */
  private void register(Listener listener) {
    getServer().getPluginManager().registerEvents(listener, this);
  }

  /**
   * Get plugin from it's name
   *
   * @param pluginName Name of plugin to get
   * @return Optional of plugin
   */
  private Optional<Plugin> getPlugin(final String pluginName) {
    return Optional.ofNullable(getServer().getPluginManager().getPlugin(pluginName));
  }
}
