package fr.epicanard.globalmarketchest.listeners;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.StatusAuction;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.utils.ConfigUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static fr.epicanard.globalmarketchest.utils.LangUtils.format;

public class PlayerListener implements Listener {

  @EventHandler
  public void onPlayerConnect(final PlayerJoinEvent event) {
    if (ConfigUtils.getBoolean("Options.Broadcast.LoginMessage.SoldAuctions", true) || ConfigUtils.getBoolean("Options.Broadcast.LoginMessage.ExpiredAuctions", true)) {
      Bukkit.getScheduler().runTaskLaterAsynchronously(GlobalMarketChest.plugin, () -> broadcastSoldAndExpiredAuctions(event.getPlayer()), 20L);
    }
    if (ConfigUtils.getBoolean("General.CheckUpdate", true) && Permissions.ADMIN_NEWVERSION.isSetOn(event.getPlayer())) {
      GlobalMarketChest.checkNewVersion(event.getPlayer());
    }
  }

  /**
   * Broadcast the number of auctions sold and expired since the last connection of the player
   *
   * @param player Player that connect
   */
  private void broadcastSoldAndExpiredAuctions(final Player player) {
    GlobalMarketChest.plugin.auctionManager.countSoldAndExpiredAuctions(player, (counts) -> {
      final Integer expiredCount = counts.getOrDefault(StatusAuction.IN_PROGRESS, 0);
      final Integer finishedCount = counts.getOrDefault(StatusAuction.FINISHED, 0);

      if (expiredCount > 0 || finishedCount > 0) {
        String message = LangUtils.get("InfoMessages.LoginMessage.BaseMessage");

        if (finishedCount > 0 && ConfigUtils.getBoolean("Options.Broadcast.LoginMessage.SoldAuctions", true)) {
          message += "\n" + format("InfoMessages.LoginMessage.SoldAuctions", "count", finishedCount);
        }
        if (expiredCount > 0 && ConfigUtils.getBoolean("Options.Broadcast.LoginMessage.ExpiredAuctions", true)) {
          message += "\n" + format("InfoMessages.LoginMessage.ExpiredAuctions", "count", expiredCount);
        }

        PlayerUtils.sendSyncMessage(player, message);
      }
    });
  }
}
