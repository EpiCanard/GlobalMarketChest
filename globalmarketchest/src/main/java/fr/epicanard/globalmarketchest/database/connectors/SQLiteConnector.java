package fr.epicanard.globalmarketchest.database.connectors;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.database.querybuilder.QueryExecutor;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.SimpleQueryBuilder;
import fr.epicanard.globalmarketchest.exceptions.ConfigException;
import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLiteConnector extends SQLConnector {
  @Getter
  private final String databaseType = "sqlite";
  private Connection connection;

  private static final String sqliteFileName = "globalmarketchest.sqlite";

  public SQLiteConnector() throws ConfigException {
    super(false);
  }

  /**
   * Create connection to database
   *
   * @return Connection
   */
  @Override
  protected Connection connect() throws ConfigException {
    try {
      File databaseFilePath = new File(GlobalMarketChest.plugin.getDataFolder(), SQLiteConnector.sqliteFileName);
      Class.forName("org.sqlite.JDBC");
      return DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new ConfigException("Can't connect to your database, please check your configuration file or the access to your database");
    }
    return null;
  }

  /**
   * Disconnection connection
   */
  @Override
  protected void disconnect(Connection connection) {
    if (connection == null)
      return;
    try {
      if (!connection.isClosed())
        connection.close();
    } catch (SQLException e) {
      return;
    }
  }

  /**
   * Get a connection from the pool or create it is no connected
   *
   * @return Connection
   */
  @Override
  public Connection getConnection() {
    return this.connection;
  }

  /**
   * Get Back the connection to the poll
   */
  @Override
  public void getBackConnection(Connection connection) {}

  /**
   * Fill pool with connection from the size specified in config file
   */
  @Override
  public void fillPool() throws ConfigException {
    this.connection = this.connect();
  }

  /**
   * Clean pool and close every connections
   */
  @Override
  public void cleanPool() {
    this.disconnect(this.connection);
  }

  /**
   * List tables used by the plugin
   *
   * @return List of tables
   */
  @Override
  public List<String> listTables() {
    final List<String> tables = new ArrayList<>();

    QueryExecutor.of().execute(new SimpleQueryBuilder(
        "SELECT * FROM sqlite_master WHERE type='table' AND name LIKE '" + DatabaseConnector.prefix + "%';",
        true
    ), res -> {
      try {
        while (res.next()) {
          tables.add(res.getString("name"));
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });

    return tables;
  }
}
