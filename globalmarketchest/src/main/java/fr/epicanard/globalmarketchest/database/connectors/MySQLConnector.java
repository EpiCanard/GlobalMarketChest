package fr.epicanard.globalmarketchest.database.connectors;

import fr.epicanard.globalmarketchest.database.querybuilder.QueryExecutor;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.SimpleQueryBuilder;
import fr.epicanard.globalmarketchest.exceptions.ConfigException;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MySQLConnector extends SQLConnector {
  @Getter
  private final String databaseType = "mysql";

  public MySQLConnector() throws ConfigException {
    super(true);
  }

  /**
   * Create connection to database
   *
   * @return Connection
   */
  @Override
  protected Connection connect() throws ConfigException {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      return DriverManager.getConnection("jdbc:mysql://" + this.buildUrl(), new Properties(this.properties));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new ConfigException("Can't connect to your database, please check your configuration file or the access to your database");
    }
    return null;
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
        "SHOW TABLES LIKE '" + DatabaseConnector.prefix + "%';",
        true
    ), res -> {
      try {
        while (res.next()) {
          tables.add(res.getString(1));
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });

    return tables;
  }
}
