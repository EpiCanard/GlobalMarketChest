package fr.epicanard.globalmarketchest.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
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

  public String joinRepeat(String str, String sep, int repeat) {
    String ret = "";
    for (int i = 0; i < repeat; i++) {
      ret += str;
      if (i != repeat - 1)
        ret += sep;
    }
    return ret;
  }

  public List<ItemStack> toItemStacks(List<AuctionInfo> auctions, BiConsumer<ItemStack, AuctionInfo> adding) {
    return auctions.stream().map(auction -> {
      ItemStack item = ItemStackUtils.getItemStack(auction.getItemStack());
      if (adding != null)
        adding.accept(item, auction);
      return item;
    }).collect(Collectors.toList());
  }
}
