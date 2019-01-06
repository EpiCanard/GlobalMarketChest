package fr.epicanard.globalmarketchest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;

public class ChatListener implements Listener {
  private Boolean isChatEditing(Player player) {
    if (player != null) {
      InventoryGUI inv = GlobalMarketChest.plugin.inventories.getInventory(player.getUniqueId());
      return (inv != null && inv.getChatEditing());
    }
    return false;
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    if (this.isChatEditing(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerBreakBlock(BlockBreakEvent  event) {
    if (this.isChatEditing(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onChatEvent(AsyncPlayerChatEvent event) {
    InventoryGUI inv = GlobalMarketChest.plugin.inventories.getInventory(event.getPlayer().getUniqueId());
    if (inv != null && inv.getChatEditing()) {
      inv.setChatReturn(event.getMessage());
      event.setCancelled(true);
    }
  }

}