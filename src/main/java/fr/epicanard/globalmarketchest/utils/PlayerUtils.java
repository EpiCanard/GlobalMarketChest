package fr.epicanard.globalmarketchest.utils;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlayerUtils {
  public OfflinePlayer getOfflinePlayer(UUID playerUUID) {
    return GlobalMarketChest.plugin.getServer().getOfflinePlayer(playerUUID);
  }

  public String getUUIDToString(Player player) {
    return player.getUniqueId().toString();
  }

  public void sendMessagePlayer(Player pl, String message) {
    pl.sendMessage("[GlobalMarketChest] " + message);
  }
}
