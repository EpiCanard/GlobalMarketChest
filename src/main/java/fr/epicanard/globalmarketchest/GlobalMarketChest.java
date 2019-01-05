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
import fr.epicanard.globalmarketchest.economy.VaultEconomy;
import fr.epicanard.globalmarketchest.exceptions.CantLoadConfigException;
import fr.epicanard.globalmarketchest.exceptions.ConfigException;
import fr.epicanard.globalmarketchest.exceptions.RequiredPluginException;
import fr.epicanard.globalmarketchest.gui.CategoryHandler;
import fr.epicanard.globalmarketchest.gui.InterfacesLoader;
import fr.epicanard.globalmarketchest.gui.InventoriesHandler;
import fr.epicanard.globalmarketchest.listeners.CloseGUICollector;
import fr.epicanard.globalmarketchest.listeners.GUIListener;
import fr.epicanard.globalmarketchest.listeners.ShopCreationListener;
import fr.epicanard.globalmarketchest.listeners.WorldListener;
import fr.epicanard.globalmarketchest.managers.AuctionManager;
import fr.epicanard.globalmarketchest.managers.ShopManager;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import lombok.Getter;


public class GlobalMarketChest extends JavaPlugin {

  @Getter
  private final ConfigLoader configLoader;
  @Getter
  private DatabaseConnection sqlConnection;
  public static GlobalMarketChest plugin;
  public final InventoriesHandler inventories;
  public final VaultEconomy economy;
  public final ShopManager shopManager;
  public final AuctionManager auctionManager;
  public final Map<String, ItemStack[]> interfaces;
  @Getter
  private CategoryHandler catHandler;

  public GlobalMarketChest() {
    // Initialization of loader
    this.configLoader = new ConfigLoader();
    this.inventories = new InventoriesHandler();
    this.economy = new VaultEconomy();
    this.shopManager = new ShopManager();
    this.auctionManager = new AuctionManager();
    this.interfaces = new HashMap<>();
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
    Utils.init();

    YamlConfiguration defConfig = this.configLoader.loadResource("interfaces.yml");
    this.interfaces.clear();
    this.interfaces.putAll(InterfacesLoader.getInstance().loadInterfaces(defConfig));

    try {
      this.initEconomy();
      this.initDatabase();
    } catch (Exception e) {
      this.disable();
      return;
    }

    this.shopManager.updateShops();

    getCommand("GlobalMarketChest").setExecutor(new CommandHandler());

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
    if (this.interfaces != null)
      this.interfaces.clear();
    HandlerList.unregisterAll(this);
  }

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
      this.sqlConnection = new MySQLConnection();
      this.sqlConnection.configFromConfigFile();
      this.sqlConnection.fillPool();
      DatabaseConnection.configureTables();
      this.sqlConnection.recreateTables();
    } catch (ConfigException e) {
      this.getLogger().log(Level.WARNING, "[SQLConnection] " + e.getMessage());
      throw new Exception();
    }
  }

  private void register(Listener listener) {
    getServer().getPluginManager().registerEvents(listener, this);
  }

  public Boolean hasPermission(Player player, String perm) {
    if (player == null || perm == null)
      return false;
    return true;
  }

}
