package fr.epicanard.globalmarketchest.Utils;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

import fr.epicanard.globalmarketchest.GlobalMarketChest;

public class PlayerUtils {
  public static OfflinePlayer getOfflinePlayer(UUID playerUUID) {
    return GlobalMarketChest.plugin.getServer().getOfflinePlayer(playerUUID);
  }
}
