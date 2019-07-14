package fr.epicanard.globalmarketchest.shops;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.metadata.FixedMetadataValue;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Class that store all the information about a shop
 */
public class ShopInfo {
  @Getter
  private int id;
  @Getter
  private String owner;
  @Getter
  private int type;
  @Getter
  private Location signLocation;
  @Getter @Setter  @NonNull
  private Location otherLocation;
  @Getter @Setter  @NonNull
  private String group;

  @Getter @Setter
  private Boolean exists = true;
  @Getter
  private String signLocationString;
  @Getter
  private String otherLocationString;
  
  public ShopInfo(ResultSet res) throws NullPointerException {
    if (res == null)
      throw new NullPointerException("Fail to get shop from database");
    try {
      this.id = res.getInt("id");
      this.owner = res.getString("owner");
      this.signLocationString = res.getString("signLocation");
      this.otherLocationString = res.getString("otherLocation");
      this.signLocation = WorldUtils.getLocationFromString(this.signLocationString, null);
      this.otherLocation = WorldUtils.getLocationFromString(this.otherLocationString, null);
      this.type = res.getInt("type");
      this.group = res.getString("group");
    } catch (SQLException e) {
      LoggerUtils.warn(e.getMessage());
    }
  }

  public ShopInfo(int id, String owner, int type, Location sign, Location other, String group) {
    this.id = id;
    this.owner = owner;
    this.type = type;
    this.signLocation = sign;
    this.otherLocation = other;
    this.signLocationString = WorldUtils.getStringFromLocation(sign);
    this.otherLocationString = WorldUtils.getStringFromLocation(other);
    this.group = group;
  }

  /**
   * Add shop metadata on specific location
   * 
   * @param loc Location where add metadata
   */
  private void addMetadata(Location loc) {
    loc.getBlock().setMetadata(ShopUtils.META_KEY, new FixedMetadataValue(GlobalMarketChest.plugin, this));
  }

  /**
   * Remove shop metadata from specific location
   * 
   * @param loca Location where remove metadata
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

  /**
   * Toggle the type in parameter to the shop mask type
   * 
   * @param type Type to toggle
   */
  public void toggleType(ShopType type) {
    this.type = type.toggle(this.type);
  }
}
