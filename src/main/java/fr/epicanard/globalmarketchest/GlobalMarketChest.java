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
import fr.epicanard.globalmarketchest.listeners.WorldListener;
import fr.epicanard.globalmarketchest.shops.ShopManager;
import fr.epicanard.globalmarketchest.world_group.WorldGroupManager;


public class GlobalMarketChest extends JavaPlugin {

  private final ConfigLoader loader;
  private DatabaseConnection sqlConnection;
  public static GlobalMarketChest plugin;
  public final InventoriesHandler inventories;
  public final VaultEconomy economy;
  public final ShopManager shopManager;
  public WorldGroupManager worldManager;
  public Map<String, ItemStack[]> interfaces;

  public GlobalMarketChest() {
    // Initialization of loader
    this.loader = new ConfigLoader();
    this.inventories = new InventoriesHandler();
    this.economy = new VaultEconomy();
    this.shopManager = new ShopManager();
    this.interfaces = new HashMap<String, ItemStack[]>();
  }
  
  public ConfigLoader getConfigLoader() {
    return this.loader;
  }
  
  public DatabaseConnection getSqlConnection() {
    return this.sqlConnection;
  }

  @Override
  public void onEnable() {
    plugin = this;


    // Load Configurations files
    this.loader.loadFiles();

    YamlConfiguration defConfig = this.loader.loadResource("interfaces.yml");
    this.interfaces = InterfacesLoader.getInstance().loadInterfaces(defConfig);

    this.worldManager = new WorldGroupManager();

    // Set Economy
    try {
      this.economy.initEconomy();
    } catch (RequiredPluginException e) {
      this.getLogger().log(Level.WARNING, e.getMessage());
      this.getLogger().log(Level.WARNING, "Plugin GlobalMarketChest disabled");
      this.setEnabled(false);
      return;
    }

    // Establish SQL Connection
    try {
      this.sqlConnection = (DatabaseConnection)new MySQLConnection();
      this.sqlConnection.configFromConfigFile();
      this.sqlConnection.fillPool();
      DatabaseConnection.configureTables();
    } catch (ConfigException e1) {
      this.getLogger().log(Level.WARNING, "[SQLConnection] " + e1.getMessage());
      this.setEnabled(false);
      return;
    }
    
    this.shopManager.updateShops();
    
/*
    try {
      this.sql = new SQLConnection("jdbc:mysql://", this.loader.getConfig());
      this.sql.listTables(this.sql.connect());
    } catch (ConfigException | SQLException e) {
      this.getLogger().log(Level.WARNING, e.getMessage());
      this.getLogger().log(Level.WARNING, "Without a correct database config the plugin can't work");
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
*/

    // Set Command Executor
    getCommand("GlobalMarketChest").setExecutor(new CommandGMC());

    // Set Listeners
    getServer().getPluginManager().registerEvents(new GUIListener(), this);
    getServer().getPluginManager().registerEvents(new CloseGUICollector(), this);
    getServer().getPluginManager().registerEvents(new WorldListener(), this);
  }
 
  public Boolean hasPermission(Player player, String perm) {
    if (player == null || perm == null)
      return false;
    
    return true;
  }
  
  public void purgeDatabase(Player p, String[] args) {
    
  }
  
  public void reloadPlugin(Player p) {
    
  }
}
