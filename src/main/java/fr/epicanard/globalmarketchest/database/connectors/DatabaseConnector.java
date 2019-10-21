package fr.epicanard.globalmarketchest.database.connectors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.ConfigException;

public abstract class DatabaseConnector {
  public final Boolean needConnection;
  protected Properties properties = new Properties();
  protected String host;
  protected String port;
  protected String database;
  protected String user;
  protected String password;

  protected static String prefix = "GMC_";
  public static String tableAuctions = "GMC_auctions";
  public static String tableShops = "GMC_shops";
  public static String tablePatches = "GMC_patches";

  DatabaseConnector(final Boolean needConnection) {
    this.needConnection = needConnection;
  }

  public static void configureTables() throws ConfigException {
    final String prefix = GlobalMarketChest.plugin.getConfigLoader().getConfig().getString("Storage.TablePrefix", "GMC_");
    if (!prefix.matches("[a-zA-Z_]*"))
      throw new ConfigException("tablePrefix not containing only letters or/and _");

    DatabaseConnector.prefix = prefix;
    DatabaseConnector.tableAuctions = prefix + "auctions";
    DatabaseConnector.tableShops = prefix + "shops";
  }

  protected String buildUrl() {
    return String.format("%s:%s/%s", this.host, this.port, this.database);
  }

  protected abstract Connection connect() throws ConfigException;

  protected abstract void disconnect(Connection connection);

  public abstract Connection getConnection();

  public abstract void getBackConnection(Connection connection);

  public abstract void fillPool() throws ConfigException;

  public abstract void cleanPool();

  public abstract void closeRessources(ResultSet res, PreparedStatement prepared);

  public abstract void configFromConfigFile() throws ConfigException;

  public abstract String buildLimit(Pair<Integer, Integer> limit);

  public abstract String getDatabaseType();

  public abstract List<String> listTables();
}
