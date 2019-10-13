package fr.epicanard.globalmarketchest.database.connectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import fr.epicanard.globalmarketchest.exceptions.ConfigException;

public class MySQLConnector extends SQLConnector {

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
   * Recreate tables if doesn't exist
   */
  @Override
  public void recreateTables() {
    Connection co = this.getConnection();

    try {
      Statement state = co.createStatement();
      state.execute(
        "CREATE TABLE IF NOT EXISTS `" + DatabaseConnector.tableAuctions + "` (" +
        "  `id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
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
        "  `id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT," +
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
    finally {
      this.getBackConnection(co);
    }
  }
}
