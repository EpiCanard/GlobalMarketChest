package fr.epicanard.globalmarketchest.database.connectors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.ConfigException;

public abstract class SQLConnector extends DatabaseConnector {
  private final LinkedBlockingQueue<Connection> pool;
  private Integer simultaneousConnections = 1;

  public SQLConnector(final Boolean needConnection) throws ConfigException {
    super(needConnection);

    this.pool = new LinkedBlockingQueue<>();
    this.simultaneousConnections = GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt("Storage.Connection.SimultaneousConnection");
    if (this.simultaneousConnections == null)
      this.simultaneousConnections = 1;
  }

  /**
   * Configure the connection automatically from config file
   */
  @Override
  public void configFromConfigFile() throws ConfigException {
    YamlConfiguration config = GlobalMarketChest.plugin.getConfigLoader().getConfig();

    this.host = config.getString("Storage.Connection.Host");
    this.port = config.getString("Storage.Connection.Port");
    this.database = config.getString("Storage.Connection.Database");
    this.user = config.getString("Storage.Connection.User");
    this.password = config.getString("Storage.Connection.Password");

    if (this.host == null || this.port == null || this.database == null || this.user == null || this.password == null)
      throw new ConfigException("Some database informations are missing");

    final String useSSL = config.getString("Storage.Connection.UseSSL");
    if (useSSL != null) {
      this.properties.put("useSSL", useSSL);
    }
    this.properties.put("autoReconnect", "true");
    this.properties.put("user", this.user);
    this.properties.put("password", this.password);
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
    try {
      Connection co = this.pool.take();
      if (co == null || !co.isValid(0) || co.isClosed())
        return this.connect();
      return co;
    } catch (SQLException | ConfigException | InterruptedException e) {
      e.printStackTrace();
    }
    return null;
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
  public void fillPool() throws ConfigException {
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
