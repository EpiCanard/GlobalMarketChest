package fr.epicanard.globalmarketchest.listeners;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.utils.ConfigUtils;
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
    if (ConfigUtils.getBoolean("Options.Broadcast.MessageLoginSoldAuctions", true)) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(GlobalMarketChest.plugin, () -> countSoldAuctions(event.getPlayer()), 20L);
    }
    if (Permissions.ADMIN_NEWVERSION.isSetOn(event.getPlayer())) {
      GlobalMarketChest.checkNewVersion(event.getPlayer());
    }
  }

  /**
   * Get the number of auctions sold since last connection of player and send message
   *
   * @param player Player that connect
   */
  private void countSoldAuctions(final Player player) {
    GlobalMarketChest.plugin.auctionManager.countSoldAuctions(player, (count) -> {
      if (count > 0) {
        PlayerUtils.sendMessage(player, format("InfoMessages.AuctionsSoldLastLogin", "auctionNumber", count));
      }
    });
  }
}
