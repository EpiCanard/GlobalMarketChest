package fr.epicanard.globalmarketchest.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;

import fr.epicanard.globalmarketchest.exceptions.ConfigException;

public class SQLConnection {

  private final String baseUrl;
  private final String host;
  private final String port;
  private final String database;
  private final String user;
  private final String password;
  private final String tablePrefix;

  public SQLConnection(String baseUrl, String host, String port, String database, String user, String password, String prefix) throws ConfigException {
    this.baseUrl = baseUrl;
    this.host = host;
    this.port = port;
    this.database = database;
    this.user = user;
    this.password = password;
    if (baseUrl == null || host == null || port == null || database == null || user == null || password == null)
      throw new ConfigException("Some database informations are not defined");
    this.tablePrefix = (prefix == null) ? "GMC_" : prefix;
    if (tablePrefix.matches("[a-zA-Z_]+") == false)
      throw new ConfigException("tablePrefix no containing only letters or/and _");
  }

  public SQLConnection(String baseUrl, FileConfiguration config) throws ConfigException {
    this.baseUrl = baseUrl;
    this.host = config.getString("Connection.host");
    this.port = config.getString("Connection.port");
    this.database = config.getString("Connection.database");
    this.user = config.getString("Connection.user");
    this.password = config.getString("Connection.password");
    if (this.baseUrl == null || this.host == null || this.port == null || this.database == null || this.user == null
        || this.password == null)
      throw new ConfigException("Some database informations are not defined");
    String prefix = config.getString("Connection.tablePrefix");
    this.tablePrefix = (prefix == null) ? "GMC_" : prefix;
    if (tablePrefix.matches("[a-zA-Z_]+") == false)
      throw new ConfigException("tablePrefix not containing only letters or/and _");
  }

  public void recreateTables() {

  }

  public Connection connect() {
    try {
      Connection con = DriverManager.getConnection(this.baseUrl + this.host + ":" + this.port + "/" + this.database,
          this.user, this.password);
      return con;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void disconnect() {

  }

}
