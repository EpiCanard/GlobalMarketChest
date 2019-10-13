package fr.epicanard.globalmarketchest.database.connectors;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.ConfigException;

public class SQLiteConnector extends SQLConnector {
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
   * Recreate tables if doesn't exist
   */
  @Override
  public void recreateTables() {
    Connection co = this.getConnection();

    try {
      Statement state = co.createStatement();
      state.execute(
        "CREATE TABLE IF NOT EXISTS `" + DatabaseConnector.tableAuctions + "` (" +
        "  `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
        "  `itemStack` VARCHAR(50) NOT NULL," +
        "  `itemMeta` TEXT," +
        "  `amount` INT UNSIGNED NOT NULL," +
        "  `price` DOUBLE NOT NULL," +
        "  `ended` BOOLEAN NOT NULL DEFAULT FALSE," +
        "  `type` TINYINT(1) NOT NULL," +
        "  `playerStarter` TEXT NOT NULL," +
        "  `playerEnder` TEXT DEFAULT NULL," +
        "  `start` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
        "  `end` TIMESTAMP DEFAULT '2000-01-01 00:00:01' NOT NULL," +
        "  `group` VARCHAR(50) NOT NULL" +
        ");"
      );
      state.execute(
        "CREATE TABLE IF NOT EXISTS `" + DatabaseConnector.tableShops + "` (" +
        "  `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
        "  `owner` TEXT NOT NULL," +
        "  `signLocation` TEXT NOT NULL," +
        "  `otherLocation` TEXT NOT NULL," +
        "  `type` TINYINT(1) NOT NULL," +
        "  `group` VARCHAR(50) NOT NULL" +
        ");"
      );
      state.close();
    } catch(SQLException e) {
      e.printStackTrace();
    }
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
    } catch (SQLException e) {}
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
}
