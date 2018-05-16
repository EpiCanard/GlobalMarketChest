package fr.epicanard.globalmarketchest.shops;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Location;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.DatabaseException;
import fr.epicanard.globalmarketchest.utils.WorldUtils;
import lombok.Getter;

public class ShopInfo {
  @Getter
  private int id;
  @Getter
  private String owner;
  @Getter
  private int type;
  @Getter
  private Location signLocation;
  @Getter
  private Location otherLocation;
  @Getter
  private String group;
  
  public ShopInfo(ResultSet res) throws NullPointerException {
    if (res == null)
      throw new NullPointerException("Fail to get shop from database");
    try {
      this.id = res.getInt("id");
      this.owner = res.getString("owner");
      this.signLocation = WorldUtils.getLocationFromString(res.getString("signLocation"), this.signLocation, "signLocation");
      this.otherLocation = WorldUtils.getLocationFromString(res.getString("otherLocation"), this.otherLocation, "otherLocation");
      this.type = res.getInt("type");
      this.group = res.getString("group");

    } catch (DatabaseException | SQLException e) {
      GlobalMarketChest.plugin.getLogger().log(Level.WARNING, e.getMessage());
    }
  }

  public ShopInfo(int id, String owner, int type, Location sign, Location other, String group) {
    this.id = id;
    this.owner = owner;
    this.type = type;
    this.signLocation = sign;
    this.otherLocation = other;
    this.group = group;
  }
}
