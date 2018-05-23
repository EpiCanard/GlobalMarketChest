package fr.epicanard.globalmarketchest.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DatabaseUtils {
  public Timestamp getTimestamp() {
    return new Timestamp(System.currentTimeMillis());
  }

  public Timestamp addDays(Timestamp ts, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(ts);
    cal.add(Calendar.DAY_OF_WEEK, days);
    return new Timestamp(cal.getTime().getTime());
  }

  public Integer getId(ResultSet res) {
    Integer id = -1;
    try {
      res.next();
      id = res.getInt(1);
    } catch(SQLException e) { e.printStackTrace(); }
    return id;
  }
}
