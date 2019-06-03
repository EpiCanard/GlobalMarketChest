package fr.epicanard.globalmarketchest.ranks;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import lombok.Getter;

public class RanksLoader {
  private RankProperties defaultRankProperties;
  private Boolean isEnabled;
  @Getter
  private Map<String, RankProperties> ranks = new HashMap<>();


  /**
   * Load ranks from config
   */
  public void loadRanks() {
    final YamlConfiguration config = GlobalMarketChest.plugin.getConfigLoader().getConfig();
    final ConfigurationSection ranksSection = config.getConfigurationSection("Ranking.Ranks");
    if (ranksSection != null) {
      final Set<String> ranksKeys =  ranksSection.getKeys(false);
      ranksKeys.forEach((rank) -> ranks.put(rank, RankProperties.of(ranksSection.getConfigurationSection(rank))));
    }
    this.defaultRankProperties = RankProperties.of(config.getConfigurationSection("Options"));
    if (this.defaultRankProperties == null) {
      this.defaultRankProperties.setLimitGlobalShopByPlayer(false);
    }
    this.isEnabled = config.getBoolean("Ranking.EnableRanks", false);
  }

  /**
   * Get final rank properties for player
   * 
   * @param player
   * @return
   */
  public RankProperties getPlayerProperties(Player player) {
    if (!this.isEnabled) {
      return this.defaultRankProperties;
    }

    final RankProperties playerRankProperties = RankProperties.of(this.defaultRankProperties);

    this.ranks.forEach((rank, properties) -> {
      if (Permissions.isSetOn(player, String.format("globalmarketchest.ranks.%s", rank))) {
        playerRankProperties.mergeRankProperties(properties);
      }
    });
    return playerRankProperties;
  }
}