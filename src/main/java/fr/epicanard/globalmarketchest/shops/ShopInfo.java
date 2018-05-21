package fr.epicanard.globalmarketchest.shops;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.metadata.FixedMetadataValue;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.DatabaseException;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
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
      this.signLocation = WorldUtils.getLocationFromString(res.getString("signLocation"), null);
      this.otherLocation = WorldUtils.getLocationFromString(res.getString("otherLocation"), null);
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

  /**
   * Add shop metadata on specific location
   */
  private void addMetadata(Location loc) {
    loc.getBlock().setMetadata(ShopUtils.META_KEY, new FixedMetadataValue(GlobalMarketChest.plugin, this));
  }

  /**
   * Remove shop metadata from specific location
   */
  private void removeMetadata(Location loc) {
    loc.getBlock().removeMetadata(ShopUtils.META_KEY, GlobalMarketChest.plugin);
  }

  /**
   * Add shop metadata on the shop location blocks
   */
  public void addMetadata() {
    Optional.ofNullable(this.signLocation).ifPresent(this::addMetadata);
    Optional.ofNullable(this.otherLocation).ifPresent(this::addMetadata);
  }

  /**
   * Remove shop metadata from the shop location blocks
   */
  public void removeMetadata() {
    Optional.ofNullable(this.signLocation).ifPresent(this::removeMetadata);
    Optional.ofNullable(this.otherLocation).ifPresent(this::removeMetadata);
  }
}
