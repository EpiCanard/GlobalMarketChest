package fr.epicanard.globalmarketchest.ranks;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class RankProperties {
  @Getter @Setter
  private Integer maxAuctionByPlayer = 0;
  @Getter @Setter
  private Integer maxGlobalShopByPlayer = 0;
  @Getter @Setter
  private Boolean limitGlobalShopByPlayer;
  @Getter @Setter
  private Integer numberDaysExpiration = 1;

  /**
   * Init a RankProperties from ConfigurationSection
   *
   * @param section ConfigurationSection on which is paremeters
   * @return New RankProperties
   */
  static public RankProperties of(ConfigurationSection section) {
    final RankProperties ret = new RankProperties();
    ret.setMaxAuctionByPlayer(section.getInt("MaxAuctionByPlayer", 0));
    if (section.contains("LimitGlobalShopByPlayer")) {
      ret.setLimitGlobalShopByPlayer(section.getBoolean("LimitGlobalShopByPlayer"));
    }
    ret.setMaxGlobalShopByPlayer(section.getInt("MaxGlobalShopByPlayer", 0));
    ret.setNumberDaysExpiration(section.getInt("NumberDaysExpiration", 1));
    return ret;
  }

  /**
   * Init a RankProperties from other RankProperties
   *
   * @param properties Other properties to clone
   * @return New RankProperties
   */
  static public RankProperties of(RankProperties properties) {
    final RankProperties ret = new RankProperties();
    ret.setMaxAuctionByPlayer(properties.getMaxAuctionByPlayer());
    ret.setLimitGlobalShopByPlayer(properties.getLimitGlobalShopByPlayer());
    ret.setMaxGlobalShopByPlayer(properties.getMaxGlobalShopByPlayer());
    ret.setNumberDaysExpiration(properties.getNumberDaysExpiration());
    return ret;
  }

  /**
   * Merge ranks properties to get final rank properties
   *
   * @param properties Properties to check and merge
   */
  public void mergeRankProperties(RankProperties properties) {
    if (properties.getMaxAuctionByPlayer() > this.maxAuctionByPlayer) {
      this.maxAuctionByPlayer = properties.getMaxAuctionByPlayer();
    }
    if (properties.getLimitGlobalShopByPlayer() != null && !properties.getLimitGlobalShopByPlayer()) {
      this.limitGlobalShopByPlayer = false;
    }
    if (properties.getMaxGlobalShopByPlayer() > this.maxGlobalShopByPlayer) {
      this.maxGlobalShopByPlayer = properties.getMaxGlobalShopByPlayer();
    }
    if (properties.getNumberDaysExpiration() > this.numberDaysExpiration) {
      this.numberDaysExpiration = properties.getNumberDaysExpiration();
    }
  }

  /**
   * Override default toString method
   */
  public String toString() {
    final StringBuilder sb = new StringBuilder("\n");
    final BiConsumer<String, String> append = (key, value) -> {
      sb.append(String.format("%s=%s\n", key, value));
    };

    append.accept("MaxAuctionByPlayer", this.maxAuctionByPlayer.toString());
    append.accept("LimitGlobalShopByPlayer", (this.limitGlobalShopByPlayer != null) ? this.limitGlobalShopByPlayer.toString() : "null");
    append.accept("MaxGlobalShopByPlayer", this.maxGlobalShopByPlayer.toString());
    append.accept("NumberDaysExpiration", this.numberDaysExpiration.toString());

    return sb.toString();
  }

  /**
   * Define if the player can create GlobalShop
   *
   * @param player Player that try to create a shop
   * @return
   */
  public Boolean canCreateShop(Player player) {
    if (this.limitGlobalShopByPlayer) {
      final String playerUuid = player.getUniqueId().toString();
      final Long numberOfShops = GlobalMarketChest.plugin.shopManager.getShops().stream().filter(shop -> shop.getOwner().equals(playerUuid)).count();
      return numberOfShops < this.maxGlobalShopByPlayer;
    }
    return true;
  }
}
