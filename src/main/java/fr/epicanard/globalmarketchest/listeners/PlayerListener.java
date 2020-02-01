package fr.epicanard.globalmarketchest.listeners;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

  @EventHandler
  public void onPlayerConnect(final PlayerJoinEvent event) {
    if (GlobalMarketChest.plugin.getConfigLoader().getConfig().getBoolean("Options.MessageLoginSoldAuctions", true)) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(GlobalMarketChest.plugin, () -> countSoldAuctions(event.getPlayer()), 20L);
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
        PlayerUtils.sendMessage(player, String.format(LangUtils.get("InfoMessages.AuctionsSoldLastLogin"), count));
      }
    });
  }
}
