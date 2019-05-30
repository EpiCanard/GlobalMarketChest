package fr.epicanard.globalmarketchest.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.database.ThrowableFunction;
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
   * Get language from config and format it with value
   * Ex : 2 days
   *
   * @param value The long value
   * @param timeLange The language vairable name
   * @return The string formatted
   */
  private String getLanguageTime(long value, String timeLang) {
    return String.format("%d %s ", value, LangUtils.get("Divers." + timeLang));
  }
  /**
   * Compare two timestamp and get the diff inside string
   * Format A : 0 days 0 hours 0 minutes (ago)
   * Format B : 0 days 0 hours (ago) |or| 0 hours 0 minutes (ago)
   *
   * @param tsA First Timestamp
   * @param tsB Second Timestamp
   * @param full Define which format use. True => Format A | False => Format B
   * @return The time string formatted
   */
  public String getExpirationString(Timestamp tsA, Timestamp tsB, Boolean full) {
    Boolean ago = false;
    StringBuilder sb = new StringBuilder();
    long diff = tsA.getTime() - tsB.getTime();

    if (diff < 0) {
      diff *= -1;
      ago = true;
    }
    diff = diff / 1000;
    long days = diff / 60 / 60 / 24;
    long hours = diff / 60 / 60 % 24;
    long minutes = diff / 60 % 60;
    long seconds = diff % 60;
    if (full || days > 0)
      sb.append(DatabaseUtils.getLanguageTime(days, "Days"));
    if (full || hours > 0)
      sb.append(DatabaseUtils.getLanguageTime(hours, "Hours"));
    if (full || (minutes > 0 && days == 0))
      sb.append(DatabaseUtils.getLanguageTime(minutes, "Minutes"));
    if (days == 0 && hours == 0 && (seconds > 0 || minutes == 0))
      sb.append(DatabaseUtils.getLanguageTime(seconds, "Seconds"));
    if (ago)
      return String.format(LangUtils.getOrElse("Divers.PastDate", "%s"), sb.toString());
    return sb.toString();
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

  /**
   * Repeat a string a certain amount of time with a separator
   *
   * @param str String to repeat
   * @param sep Separator
   * @param repeat Repeat number
   * @return Final composed string
   */
  public String joinRepeat(String str, String sep, int repeat) {
    String ret = "";
    for (int i = 0; i < repeat; i++) {
      ret += str;
      if (i != repeat - 1)
        ret += sep;
    }
    return ret;
  }

  /**
   * Convert a list of auction into a list of itemstacks
   *
   * @param auctions
   * @param adding Biconsumer to apply modifications to the itemstack
   * @return A list of itemstack
   */
  public List<ItemStack> toItemStacks(List<AuctionInfo> auctions, BiConsumer<ItemStack, AuctionInfo> adding) {
    return auctions.stream().map(auction -> {
      ItemStack item = DatabaseUtils.deserialize(auction.getItemMeta());
      if (item == null) {
        LoggerUtils.warn(String.format("Wrong itemMeta for auction `%d`", auction.getId()));
      } else {
        item.setAmount(ItemStackUtils.getMaxStack(item, auction.getAmount()));
        if (adding != null)
          adding.accept(item, auction);
      }
      return item;
    }).filter(i -> i != null).collect(Collectors.toList());
  }

  /**
   * Serialize an itemstack into string
   *
   * @param item
   * @return return item serialized
   */
  public String serialize(ItemStack item) {
    YamlConfiguration yaml = new YamlConfiguration();
    yaml.set("item", item);
    return yaml.saveToString();
  }

  /**
   * Deserialize an itemstack from string
   *
   * @param content
   * @return return item deserialized
   */
  public ItemStack deserialize(String content) {
    YamlConfiguration yaml = new YamlConfiguration();
    try {
      yaml.loadFromString(content);
      return yaml.getItemStack("item");
    } catch (InvalidConfigurationException e) {
      return null;
    }
  }

  /**
   * Get a field inside a ResultSet or return null if not exist
   * 
   * @param <T> Return type of callback
   * @param field Field name to get
   * @param callback Callback that get the field
   * @return
   */
  public <T> T getField(String field, ThrowableFunction<String, T, SQLException> callback) {
    try {
      return callback.apply(field);
    } catch (SQLException e) {
      return null;
    }
  }
}
