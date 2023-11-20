package fr.epicanard.globalmarketchest.shops;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Class that store all the information about a shop
 */
public class ShopInfo {
  @Getter
  private int id;
  @Getter
  private String owner;
  @Getter @Setter
  private ShopType type;
  @Getter
  private Optional<Location> signLocation;
  @Getter @Setter
  private Optional<Location> otherLocation;
  @Getter @Setter
  private Optional<Location> tpLocation;
  @Getter @Setter  @NonNull
  private String group;
  @Getter
  private String server;

  @Getter @Setter
  private Boolean exists = true;

  public ShopInfo(ResultSet res) throws NullPointerException {
    if (res == null)
      throw new NullPointerException("Fail to get shop from database");
    try {
      this.id = res.getInt("id");
      this.owner = res.getString("owner");
      this.signLocation = Optional.ofNullable(res.getString("signLocation")).map(WorldUtils::getLocationFromString);
      this.otherLocation = Optional.ofNullable(res.getString("otherLocation")).map(WorldUtils::getLocationFromString);
      this.tpLocation = Optional.ofNullable(res.getString("tpLocation")).map(WorldUtils::getLocationFromString);
      this.type = ShopType.fromId(res.getInt("type"));
      this.group = res.getString("group");
      this.server = res.getString("server");
    } catch (SQLException e) {
      LoggerUtils.warn(e.getMessage());
    }
  }

  public ShopInfo(int id, String owner, ShopType type, Location sign, Location other, Location tp, String group) {
    this.id = id;
    this.owner = owner;
    this.type = type;
    this.signLocation = Optional.ofNullable(sign);
    this.otherLocation = Optional.ofNullable(other);
    this.tpLocation = Optional.ofNullable(tp);
    this.group = group;
    this.server = GlobalMarketChest.plugin.getServerName();
  }

  /**
   * Add shop metadata on the shop location blocks
   */
  public void addMetadata() {
    this.signLocation.ifPresent(this::addMetadata);
    this.otherLocation.ifPresent(this::addMetadata);
  }

  /**
   * Add shop metadata on specific location
   *
   * @param loc Location where add metadata
   */
  public void addMetadata(Location loc) {
    loc.getBlock().setMetadata(ShopUtils.META_KEY, new FixedMetadataValue(GlobalMarketChest.plugin, this));
  }

  /**
   * Remove shop metadata from the shop location blocks
   */
  public void removeMetadata() {
    this.signLocation.ifPresent(this::removeMetadata);
    this.otherLocation.ifPresent(this::removeMetadata);
  }

  /**
   * Remove shop metadata from specific location
   *
   * @param loc Location where remove metadata
   */
  public void removeMetadata(Location loc) {
    loc.getBlock().removeMetadata(ShopUtils.META_KEY, GlobalMarketChest.plugin);
  }

  /**
   * Return sign location or other location if sign location is missing
   */
  public Optional<Location> getLocation() {
    return this.signLocation.isPresent() ? this.signLocation : this.otherLocation;
  }

  /**
   * Return sign location formatted
   */
  public String getRawSignLocation() {
    return this.signLocation.map(WorldUtils::getStringFromLocation).orElse("");
  }

  /**
   * Return other location formatted
   */
  public String getRawOtherLocation() {
    return this.otherLocation.map(WorldUtils::getStringFromLocation).orElse("");
  }

  /**
   * Return other location formatted
   */
  public String getRawTpLocation() {
    return this.tpLocation.map(WorldUtils::getStringFromLocation).orElse("");
  }

  /**
   * Return sign location or other location if sign location is missing formatted
   */
  public String getRawLocation() {
    return this.getLocation().map(WorldUtils::getStringFromLocation).orElse("");
  }
}
