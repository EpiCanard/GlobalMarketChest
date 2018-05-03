package fr.epicanard.globalmarketchest.database.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.configuration.file.YamlConfiguration;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.ConfigException;

public class MySQLConnection extends DatabaseConnection {
  private final LinkedBlockingQueue<Connection> pool;
  private Integer simultaneousConnections = 1;

  public MySQLConnection() throws ConfigException {
    super();

    this.pool = new LinkedBlockingQueue<Connection>();
    this.simultaneousConnections = GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt("Connection.SimultaneousConnection");
    if (this.simultaneousConnections == null)
      this.simultaneousConnections = 1;
  }

  public void recreateTables() {

  }

  public void listTables(Connection con) throws SQLException {
    Statement st = con.createStatement();
    ResultSet rs = st.executeQuery("SHOW TABLES IN " + this.database);
    while (rs.next()) {
      System.out.println(rs.getString(1));
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
   * Configure the connection manually sending all informations in parameters
   */
  @Override
  public void configManually(String host, String port, String database, String user, String password) throws ConfigException {
    if (this.host == null || this.port == null || this.database == null || this.user == null
        || this.password == null)
      throw new ConfigException("Some database informations are missing");

    this.host = host;
    this.port = port;
    this.database = database;
    this.user = user;
    this.password = password;
  }

  @Override
  protected Connection connect() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      Connection con = DriverManager.getConnection("jdbc:mysql://" + this.buildUrl(),
          this.user, this.password);
      return con;
    } catch (SQLException e) {
      e.printStackTrace();
  	} catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  @Override
  protected void disconnect(Connection connection) {
    if (connection != null) {
      try {
        if (!connection.isClosed())
          connection.close();
      } catch (SQLException e) {}      
    }
  }

  @Override
  public Connection getConnection() {
    try {
      Connection co = this.pool.take();
      if (co == null || co.isClosed())
        return this.connect();
      return co;
    } catch (SQLException e) {
      return this.connect();
    } catch (InterruptedException e) {
      return this.connect();
    }
  }

  @Override
  public void getBackConnection(Connection connection) {
    if (connection != null) {
      try {
        if (this.pool.size() > this.simultaneousConnections)
          this.disconnect(connection);
        else
          this.pool.put(connection);
      } catch (InterruptedException e) {}      
    }
  }

  @Override
  public void fillPool() {
    for (int i = 0; i < this.simultaneousConnections; i++) {
      Connection co = this.connect();
      this.getBackConnection(co);
    }
  }

  @Override
  public void closeRessources(ResultSet res, PreparedStatement prepared) {
    if (res != null) {
      try {
        res.close();
      } catch (SQLException e) {}
    }
    if (prepared != null) {
      try {
        prepared.close();
      } catch (SQLException e) {}
    }
  }
}
