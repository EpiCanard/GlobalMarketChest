package fr.epicanard.globalmarketchest.utils;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;

public class PlayerUtils {
  public static OfflinePlayer getOfflinePlayer(UUID playerUUID) {
    return GlobalMarketChest.plugin.getServer().getOfflinePlayer(playerUUID);
  }

  public static void sendMessagePlayer(Player pl, String message) {
    pl.sendMessage("[GlobalMarketChest] " + message);
  }
}
