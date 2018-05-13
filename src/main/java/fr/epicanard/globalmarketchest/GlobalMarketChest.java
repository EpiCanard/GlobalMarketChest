package fr.epicanard.globalmarketchest;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.commands.CommandGMC;
import fr.epicanard.globalmarketchest.configuration.ConfigLoader;
import fr.epicanard.globalmarketchest.database.connections.DatabaseConnection;
import fr.epicanard.globalmarketchest.database.connections.MySQLConnection;
import fr.epicanard.globalmarketchest.economy.VaultEconomy;
import fr.epicanard.globalmarketchest.exceptions.ConfigException;
import fr.epicanard.globalmarketchest.exceptions.RequiredPluginException;
import fr.epicanard.globalmarketchest.gui.InterfacesLoader;
import fr.epicanard.globalmarketchest.gui.InventoriesHandler;
import fr.epicanard.globalmarketchest.listeners.CloseGUICollector;
import fr.epicanard.globalmarketchest.listeners.GUIListener;
import fr.epicanard.globalmarketchest.listeners.ShopCreationListener;
import fr.epicanard.globalmarketchest.listeners.WorldListener;
import fr.epicanard.globalmarketchest.managers.ShopManager;
import fr.epicanard.globalmarketchest.world_group.WorldGroupManager;
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
  public WorldGroupManager worldManager;
  public Map<String, ItemStack[]> interfaces;

  public GlobalMarketChest() {
    // Initialization of loader
    this.configLoader = new ConfigLoader();
    this.inventories = new InventoriesHandler();
    this.economy = new VaultEconomy();
    this.shopManager = new ShopManager();
    this.interfaces = new HashMap<String, ItemStack[]>();
  }

  @Override
  public void onEnable() {
    plugin = this;

    this.configLoader.loadFiles();

    YamlConfiguration defConfig = this.configLoader.loadResource("interfaces.yml");
    this.interfaces = InterfacesLoader.getInstance().loadInterfaces(defConfig);

    this.worldManager = new WorldGroupManager();

    try {
      this.initEconomy();
      this.initDatabase();
    } catch (Exception e) {
      return;
    }
    
    this.shopManager.updateShops();

    getCommand("GlobalMarketChest").setExecutor(new CommandGMC());

    getServer().getPluginManager().registerEvents(new GUIListener(), this);
    getServer().getPluginManager().registerEvents(new CloseGUICollector(), this);
    getServer().getPluginManager().registerEvents(new WorldListener(), this);
    getServer().getPluginManager().registerEvents(new ShopCreationListener(), this);
  }

  @Override
  public void onDisable() {
    this.sqlConnection.cleanPool();
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
      this.getLogger().log(Level.WARNING, "Plugin GlobalMarketChest disabled");
      this.setEnabled(false);
      throw new Exception();
    }
  }

  /**
   * Init Database create databaseconnection and configure it
   * @throws Exception
   */
  private void initDatabase() throws Exception {
    try {
      this.sqlConnection = (DatabaseConnection)new MySQLConnection();
      this.sqlConnection.configFromConfigFile();
      this.sqlConnection.fillPool();
      DatabaseConnection.configureTables();
      this.sqlConnection.recreateTables();
    } catch (ConfigException e) {
      this.getLogger().log(Level.WARNING, "[SQLConnection] " + e.getMessage());
      this.setEnabled(false);
      throw new Exception();
    }
  }
 
  public Boolean hasPermission(Player player, String perm) {
    if (player == null || perm == null)
      return false;
    
    return true;
  }

}
