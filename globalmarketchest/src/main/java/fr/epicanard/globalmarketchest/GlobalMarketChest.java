package fr.epicanard.globalmarketchest;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.epicanard.globalmarketchest.commands.CommandHandler;
import fr.epicanard.globalmarketchest.configuration.ConfigLoader;
import fr.epicanard.globalmarketchest.configuration.PriceLimit;
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
import fr.epicanard.globalmarketchest.nms.*;
import fr.epicanard.globalmarketchest.ranks.RanksLoader;
import fr.epicanard.globalmarketchest.utils.*;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
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
  @Getter
  private Map<String, PriceLimit> priceLimits;
  @Getter
  private Double tax;
  @Getter
  private String serverName;
  @Getter
  private Nms nms;

  public GlobalMarketChest() {
    GlobalMarketChest.plugin = this;

    // Initialization of loader
    this.configLoader = new ConfigLoader();
    this.inventories = new InventoriesHandler();
    this.economy = new VaultEconomy();
    this.shopManager = new ShopManager();
    this.auctionManager = new AuctionManager();
    this.ranksLoader = new RanksLoader();
    this.nms = initNms();

    Version.initVersion(Utils.getFullVersion());
  }

  @Override
  public void onEnable() {

    if (this.nms == null) {
      LoggerUtils.error("Version " + Utils.getFullVersion() + " is not yet supported. Please contact EpiCanard to add support of this version.");
      this.disable();
      return;
    }

    try {
      this.configLoader.loadFiles();
    } catch (CantLoadConfigException e) {
      this.getLogger().log(Level.SEVERE, e.getMessage());
      this.disable();
      return;
    }
    this.serverName = ConfigUtils.getString("MultiServer.ServerName", "default");

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

    this.priceLimits = this.configLoader.loadPriceLimitConfig();

    Double taxMultiplier = ConfigUtils.getDouble("Options.Tax", 0);
    if (taxMultiplier < 0 || taxMultiplier > 100) {
      LoggerUtils.warn(String.format("Options.Tax must be between 0 and 100. Actual value: %f", taxMultiplier));
      this.disable();
      return;
    }
    this.tax = taxMultiplier;

    getCommand("GlobalMarketChest").setExecutor(new CommandHandler());

    this.register(new ChatListener());
    this.register(new GUIListener());
    this.register(new CloseGUICollector());
    this.register(WorldListener.of());
    this.register(new ShopCreationListener());
    this.register(new PlayerListener());
    final Listener moneyExchange = getPlugin("MysqlPlayerDataBridge")
        .filter(DataBridgeListener::canBeEnabled)
        .map(dataBridge -> (Listener) new DataBridgeListener(dataBridge))
        .orElseGet(MoneyExchangeListener::new);
    this.register(moneyExchange);

    if (ConfigUtils.getBoolean("General.Metrics", true)) {
      new Metrics(this, 7557);
    }

    if (ConfigUtils.getBoolean("General.CheckUpdate", true)) {
      GlobalMarketChest.checkNewVersion(this.getServer().getConsoleSender());
    }

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

  /**
   * Check if a new version of plugin is available
   * Request https://api.spiget.org/v2/resources/64921/versions/latest to get the last version of plugin
   *
   * @param sender Player to send the message
   */
  public static void checkNewVersion(final CommandSender sender) {
    try (Scanner s = new Scanner(new URL("https://api.spiget.org/v2/resources/64921/versions/latest").openStream())) {
      final String value = s.useDelimiter("\\A").next();
      final JsonObject obj = (JsonObject) new JsonParser().parse(value);
      final String lastVersion = obj.get("name").getAsString();
      final String currentVersion = GlobalMarketChest.plugin.getDescription().getVersion();

      if (!currentVersion.equals(lastVersion)) {
        PlayerUtils.sendMessage(sender, LangUtils.format("InfoMessages.NewVersionAvailable", ImmutableMap.of(
                "lastVersion", lastVersion,
                "currentVersion", currentVersion)));
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  private static Nms initNms() {
    switch (Utils.getFullVersion()) {
      case "1.12":
      case "1.12.1":
      case "1.12.2":
        return new Nms_v1_12_R1();
      case "1.13":
      case "1.13.1":
        return new Nms_v1_13_R1();
      case "1.13.2":
        return new Nms_v1_13_R2();
      case "1.14":
      case "1.14.1":
      case "1.14.2":
      case "1.14.3":
      case "1.14.4":
        return new Nms_v1_14_R1();
      case "1.15":
      case "1.15.1":
      case "1.15.2":
        return new Nms_v1_15_R1();
      case "1.16":
      case "1.16.1":
        return new Nms_v1_16_R1();
      case "1.16.2":
      case "1.16.3":
        return new Nms_v1_16_R2();
      case "1.16.4":
      case "1.16.5":
        return new Nms_v1_16_R3();
      case "1.17":
      case "1.17.1":
        return new Nms_v1_17_R1();
      case "1.18":
      case "1.18.1":
        return new Nms_v1_18_R1();
      case "1.18.2":
        return new Nms_v1_18_R2();
      case "1.19":
      case "1.19.1":
      case "1.19.2":
        return new Nms_v1_19_R1();
      case "1.19.3":
        return new Nms_v1_19_R2();
      case "1.19.4":
        return new Nms_v1_19_R3();
      case "1.20":
      case "1.20.1":
        return new Nms_v1_20_R1();
      case "1.20.2":
        return new Nms_v1_20_R2();
      case "1.20.3":
      case "1.20.4":
        return new Nms_v1_20_R3();
      case "1.20.5":
      case "1.20.6":
        return new Nms_v1_20_R4();
      case "1.21":
        return new Nms_v1_21_R1();
      default:
        return null;
    }
  }
}
