package fr.epicanard.globalmarketchest;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.commands.CommandHandler;
import fr.epicanard.globalmarketchest.configuration.ConfigLoader;
import fr.epicanard.globalmarketchest.database.connections.DatabaseConnection;
import fr.epicanard.globalmarketchest.database.connections.MySQLConnection;
import fr.epicanard.globalmarketchest.database.connections.SQLiteConnection;
import fr.epicanard.globalmarketchest.economy.VaultEconomy;
import fr.epicanard.globalmarketchest.exceptions.CantLoadConfigException;
import fr.epicanard.globalmarketchest.exceptions.ConfigException;
import fr.epicanard.globalmarketchest.exceptions.RequiredPluginException;
import fr.epicanard.globalmarketchest.gui.CategoryHandler;
import fr.epicanard.globalmarketchest.gui.InterfacesLoader;
import fr.epicanard.globalmarketchest.gui.InventoriesHandler;
import fr.epicanard.globalmarketchest.listeners.ChatListener;
import fr.epicanard.globalmarketchest.listeners.CloseGUICollector;
import fr.epicanard.globalmarketchest.listeners.GUIListener;
import fr.epicanard.globalmarketchest.listeners.ShopCreationListener;
import fr.epicanard.globalmarketchest.listeners.WorldListener;
import fr.epicanard.globalmarketchest.managers.AuctionManager;
import fr.epicanard.globalmarketchest.managers.ShopManager;
import fr.epicanard.globalmarketchest.ranks.RanksLoader;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import lombok.Getter;


public class GlobalMarketChest extends JavaPlugin {

  @Getter
  private final ConfigLoader configLoader;
  @Getter
  private final RanksLoader ranksLoader;
  @Getter
  private DatabaseConnection sqlConnection;
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
    } catch (Exception e) {
      this.disable();
      return;
    }

    this.ranksLoader.loadRanks();
    this.shopManager.updateShops();

    getCommand("GlobalMarketChest").setExecutor(new CommandHandler());

    this.register(new ChatListener());
    this.register(new GUIListener());
    this.register(new CloseGUICollector());
    this.register(new WorldListener());
    this.register(new ShopCreationListener());
  }

  @Override
  public void onDisable() {
    if (this.sqlConnection != null)
      this.sqlConnection.cleanPool();
    if (this.inventories != null)
      this.inventories.removeAllInventories();
    HandlerList.unregisterAll(this);
  }

  /**
   * Disable the plugin
   */
  public void disable() {
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
   * @throws Exception
   */
  private void initEconomy() throws Exception {
    try {
      this.economy.initEconomy();
    } catch (RequiredPluginException e) {
      this.getLogger().log(Level.WARNING, e.getMessage());
      throw new Exception();
    }
  }

  /**
   * Init Database create databaseconnection and configure it
   * @throws Exception
   */
  private void initDatabase() throws Exception {
    try {
      this.sqlConnection = this.getDatabaseConnectionProvider();
      if (this.sqlConnection.needConnection) {
        this.sqlConnection.configFromConfigFile();
      }
      this.sqlConnection.fillPool();
      DatabaseConnection.configureTables();
      this.sqlConnection.recreateTables();
    } catch (ConfigException e) {
      this.getLogger().log(Level.WARNING, "[SQLConnection] " + e.getMessage());
      throw new Exception();
    }
  }

  /**
   * Init Database connection provider depending of config 'Storage.Type'
   *
   * @return Return the correct Database connection provider
   * @throws ConfigException
   */
  private DatabaseConnection getDatabaseConnectionProvider() throws ConfigException {
    final String connectionType = this.configLoader.getConfig().getString("Storage.Type");
    switch (connectionType) {
      case "sqlite":
        return new SQLiteConnection();
      case "mysql":
        return new MySQLConnection();
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
}
