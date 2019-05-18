package fr.epicanard.globalmarketchest.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

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

  /**
   * Prevent the execution of commands inside chat when th player is in chat mode
   * 
   * @param event Evnt PlayerCommandPreprocessEvent
   */
  @EventHandler
  public void preProcessCommand(PlayerCommandPreprocessEvent event) {
    if (event.getPlayer() != null) {
      final UUID uuidPlayer = event.getPlayer().getUniqueId();
      if (GlobalMarketChest.plugin.inventories.hasInventory(uuidPlayer) && GlobalMarketChest.plugin.inventories.getInventory(uuidPlayer).getChatEditing()) {
        event.setCancelled(true);
        PlayerUtils.sendMessageConfig(event.getPlayer(), "ErrorMessages.CommandInChat");
      }
    }
  }
}