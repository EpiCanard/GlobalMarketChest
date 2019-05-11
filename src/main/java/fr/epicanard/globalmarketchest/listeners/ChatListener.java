package fr.epicanard.globalmarketchest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;

public class ChatListener implements Listener {
  /**
   * Verify if the chat is in editing mode for current player
   * 
   * @param player Player linked to inventory
   * @return Boolean that define if chat editing mode is enabled
   */
  private Boolean isChatEditing(Player player) {
    if (player != null) {
      InventoryGUI inv = GlobalMarketChest.plugin.inventories.getInventory(player.getUniqueId());
      return (inv != null && inv.getChatEditing());
    }
    return false;
  }

  /**
   * Disable moving event if player is in chat editing mode
   * 
   * @param event Event
   */
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    if (this.isChatEditing(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  /**
   * Disable plauyer break event if player is in chat editing mode
   * 
   * @param event Event
   */
  @EventHandler
  public void onPlayerBreakBlock(BlockBreakEvent  event) {
    if (this.isChatEditing(event.getPlayer())) {
      event.setCancelled(true);
    }
  }
}