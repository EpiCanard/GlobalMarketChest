package fr.epicanard.globalmarketchest.database_connections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.ConfigException;

public abstract class DatabaseConnection {
  protected String host;
  protected String port;
  protected String database;
  protected String user;
  protected String password;

  public static String tableAuctions = "GMC_auctions";
  public static String tableBannedItems = "GMC_bannedItems";
  public static String tableCategories = "GMC_categories";
  public static String tableCategoryItems = "GMC_categoryItems";
  public static String tableItemstacks = "GMC_itemstacks";
  public static String tablePlayers = "GMC_players";
  public static String tableShops = "GMC_shops";
  
  public static void configureTables() throws ConfigException  {
    String prefix = GlobalMarketChest.plugin.getConfigLoader().getConfig().getString("Connection.TablePrefix");
    if (prefix == null)
      return;
    if (prefix.matches("[a-zA-Z_]*") == false)
      throw new ConfigException("tablePrefix not containing only letters or/and _");
  
    DatabaseConnection.tableAuctions = prefix + "auctions";
    DatabaseConnection.tableBannedItems = prefix + "bannedItems";
    DatabaseConnection.tableCategories = prefix + "categories";
    DatabaseConnection.tableCategoryItems = prefix + "categoryItems";
    DatabaseConnection.tableItemstacks = prefix + "itemstacks";
    DatabaseConnection.tablePlayers = prefix + "players";
    DatabaseConnection.tableShops = prefix + "shops";
  }
  
  protected String buildUrl() {
    return this.host + ":" + this.port + "/" + this.database;
  }
  
  protected abstract Connection connect();
  protected abstract void disconnect(Connection connection);
  public abstract Connection getConnection();
  public abstract void getBackConnection(Connection connection);
  public abstract void fillPool();
  public abstract void closeRessources(ResultSet res, PreparedStatement prepared);

  public abstract void configFromConfigFile() throws ConfigException;
  public abstract void configManually(String host, String port, String database, String user, String password) throws ConfigException;  
}
