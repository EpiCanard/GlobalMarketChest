package fr.epicanard.globalmarketchest.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import fr.epicanard.globalmarketchest.GlobalMarketChest;

public class DatabaseUtils {
  public static void printTable(String tableName) {
    try {
      Connection co = GlobalMarketChest.plugin.getSqlConnection().getConnection();
      PreparedStatement prepared = co.prepareStatement("SELECT * FROM " + tableName);
      ResultSet res = prepared.executeQuery();
      int j = 0;
      while (res.next()) {
        j = res.getMetaData().getColumnCount();
        for (int i = 1; i <= j; i++) {
          System.out.println(res.getMetaData().getColumnName(i) + " : " + res.getString(i));
        }
        System.out.println("-_-_-_-_-_-_-_-");
      }
      GlobalMarketChest.plugin.getSqlConnection().closeRessources(res, prepared);
    } catch (SQLException e) {
      return;
    }

  }

  public static Timestamp getTimestamp() {
    return new Timestamp(System.currentTimeMillis());
  }

  public static Timestamp addDays(Timestamp ts, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(ts);
    cal.add(Calendar.DAY_OF_WEEK, days);
    return new Timestamp(cal.getTime().getTime());
  }
}
