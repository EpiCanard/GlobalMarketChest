package fr.epicanard.globalmarketchest.utils;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import lombok.experimental.UtilityClass;

/**
 * Utility Class for player action
 */
@UtilityClass
public class PlayerUtils {
  /**
   * Get a player forrom is UUID
   */
  public OfflinePlayer getOfflinePlayer(UUID playerUUID) {
    return GlobalMarketChest.plugin.getServer().getOfflinePlayer(playerUUID);
  }

  /**
   * Get the uuid of a player into string
   * 
   * @param player
   * @return 
   */
  public String getUUIDToString(Player player) {
    return player.getUniqueId().toString();
  }

  /**
   * Send Message to a player
   * 
   * @param player
   * @param message message to sent to player
   */
  public void sendMessagePlayer(Player pl, String message) {
    pl.sendMessage("[GlobalMarketChest] " + message);
  }
}
