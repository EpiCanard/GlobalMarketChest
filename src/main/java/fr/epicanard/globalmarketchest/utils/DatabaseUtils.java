package fr.epicanard.globalmarketchest.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public class DatabaseUtils {
  public static Timestamp getTimestamp() {
    return new Timestamp(System.currentTimeMillis());
  }

  public static Timestamp addDays(Timestamp ts, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(ts);
    cal.add(Calendar.DAY_OF_WEEK, days);
    return new Timestamp(cal.getTime().getTime());
  }

  public static Integer getId(ResultSet res) {
    Integer id = -1;
    try {
      res.next();
      id = res.getInt(1);
    } catch(SQLException e) { e.printStackTrace(); }
    return id;
  }
}
