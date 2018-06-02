package fr.epicanard.globalmarketchest.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import lombok.experimental.UtilityClass;

/**
 * Utiity Class for database actions
 */
@UtilityClass
public class DatabaseUtils {
  /**
   * Get current timestamp
   * 
   * @return Timestamp
   */
  public Timestamp getTimestamp() {
    return new Timestamp(System.currentTimeMillis());
  }

  /**
   * Add days to a spécific timestamp
   * 
   * @param ts    Timestamp used
   * @param days  Nummber of days to add
   * @return Return the new timestamp
   */
  public Timestamp addDays(Timestamp ts, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(ts);
    cal.add(Calendar.DAY_OF_WEEK, days);
    return new Timestamp(cal.getTime().getTime());
  }

  /**
   * Get an id from resultSet
   * 
   * @param res ResultSet
   * @return Return the id or -1
   */
  public Integer getId(ResultSet res) {
    Integer id = -1;
    try {
      res.next();
      id = res.getInt(1);
    } catch(SQLException e) { e.printStackTrace(); }
    return id;
  }
}
