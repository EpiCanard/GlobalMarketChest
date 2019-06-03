package fr.epicanard.globalmarketchest.ranks;

import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import lombok.Setter;

public class RankProperties {
  @Getter @Setter
  private Integer maxAuctionByPlayer = 0;
  @Getter @Setter
  private Integer maxGlobalShopByPlayer = 0;
  @Getter @Setter
  private Boolean limitGlobalShopByPlayer;

  /**
   * Init a RankProperties from ConfigurationSection
   * 
   * @param section ConfigurationSection on which is paremeters
   * @return New RankProperties
   */
  static public RankProperties of(ConfigurationSection section) {
    final RankProperties ret = new RankProperties();
    ret.setMaxAuctionByPlayer(section.getInt("MaxAuctionByPlayer", 0));
    ret.setLimitGlobalShopByPlayer(section.getBoolean("LimitGlobalShopByPlayer"));
    ret.setMaxGlobalShopByPlayer(section.getInt("MaxGlobalShopByPlayer", 0));
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
  }
}