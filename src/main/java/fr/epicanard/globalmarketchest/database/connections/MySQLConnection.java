package fr.epicanard.globalmarketchest.database.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.ConfigException;

public class MySQLConnection extends DatabaseConnection {
  private final LinkedBlockingQueue<Connection> pool;
  private Integer simultaneousConnections = 1;

  public MySQLConnection() throws ConfigException {
    super();

    this.pool = new LinkedBlockingQueue<>();
    this.simultaneousConnections = GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt("Connection.SimultaneousConnection");
    if (this.simultaneousConnections == null)
      this.simultaneousConnections = 1;
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
        "CREATE TABLE IF NOT EXISTS `" + DatabaseConnection.tableAuctions + "` (" +
        "  `id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "  `itemStack` VARCHAR(50) NOT NULL," +
        "  `damage` SMALLINT NOT NULL," +
        "  `itemMeta` TEXT," +
        "  `amount` INT UNSIGNED NOT NULL," +
        "  `price` DOUBLE NOT NULL," +
        "  `state` TINYINT(1) NOT NULL," +
        "  `type` TINYINT(1) NOT NULL," +
        "  `playerStarter` TEXT NOT NULL," +
        "  `playerEnder` TEXT DEFAULT NULL," +
        "  `start` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
        "  `end` TIMESTAMP NOT NULL," +
        "  `group` VARCHAR(50) NOT NULL" +
        ");"
      );
      state.execute(
        "CREATE TABLE IF NOT EXISTS `" + DatabaseConnection.tableShops + "` (" +
        "  `id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
        "  `owner` TEXT NOT NULL," +
        "  `signLocation` TEXT NOT NULL," +
        "  `otherLocation` TEXT NOT NULL," +
        "  `type` TINYINT(1) NOT NULL," +
        "  `group` VARCHAR(50) NOT NULL" +
        ");"
      );
      state.close();
    } catch(SQLException e) {}
    finally {
      this.getBackConnection(co);
    }
  }

  /**
   * Configure the connection automatically from config file
   */
  @Override
  public void configFromConfigFile() throws ConfigException {
    YamlConfiguration config = GlobalMarketChest.plugin.getConfigLoader().getConfig();

    this.host = config.getString("Connection.Host");
    this.port = config.getString("Connection.Port");
    this.database = config.getString("Connection.Database");
    this.user = config.getString("Connection.User");
    this.password = config.getString("Connection.Password");
    
    if (this.host == null || this.port == null || this.database == null || this.user == null
        || this.password == null)
      throw new ConfigException("Some database informations are missing");
  }

  /**
   * Create connection to database
   * @return Connection
   */
  @Override
  protected Connection connect() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      return DriverManager.getConnection("jdbc:mysql://" + this.buildUrl(),
          this.user, this.password);
    } catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
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
    } catch (SQLException e) {}      
  }

  /**
   * Get a connection from the pool or create it is no connected
   * @return Connection
   */
  @Override
  public Connection getConnection() {
    try {
      Connection co = this.pool.take();
      if (co == null || co.isClosed())
        return this.connect();
      return co;
    } catch (SQLException | InterruptedException e) {
      return this.connect();
    }
  }

  /**
   * Get Back the connection to the poll
   */
  @Override
  public void getBackConnection(Connection connection) {
    if (connection == null)
      return;
    try {
      if (this.pool.size() >= this.simultaneousConnections)
        this.disconnect(connection);
      else
        this.pool.put(connection);
    } catch (InterruptedException e) {}      
  }

  /**
   * Fill pool with connection from the size specified in config file
   */
  @Override
  public void fillPool() {
    for (int i = 0; i < this.simultaneousConnections; i++) {
      Connection co = this.connect();
      this.getBackConnection(co);
    }
  }

  /**
   * Clean pool and close every connections
   */
  @Override
  public void cleanPool() {
    Connection co;
    while ((co = this.pool.poll()) != null)
      this.disconnect(co);
  }

  /**
   * Close ressources specified in parameter
   */
  @Override
  public void closeRessources(ResultSet res, PreparedStatement prepared) {
    try {
      if (res != null)
        res.close();
      if (prepared != null)
        prepared.close();
    } catch (SQLException e) {}
  }

  @Override
  public String buildLimit(Pair<Integer, Integer> limit) {
    return String.format("LIMIT %d, %d", limit.getLeft(), limit.getRight());
  }
}
