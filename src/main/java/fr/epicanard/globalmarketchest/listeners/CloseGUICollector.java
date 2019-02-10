package fr.epicanard.globalmarketchest.listeners;

import java.util.UUID;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

/**
 * Listener for every close event link to an inventory
 */
public class CloseGUICollector implements Listener {

  /**
   * Remove Inventory GUI
   * If remove is false, it check that the player is not in chat editing mode
   *
   * @param player Player that have inventory
   * @param remove Boolean to define if must check chat editing mode
   */
  private void closeGUI(HumanEntity player, Boolean remove) {
    if (player != null && player instanceof Player) {
      UUID playerID = player.getUniqueId();
      if (GlobalMarketChest.plugin.inventories.hasInventory(playerID)) {
        if (remove || !GlobalMarketChest.plugin.inventories.getInventory(playerID).getChatEditing())
          GlobalMarketChest.plugin.inventories.removeInventory(playerID);
      }
    }
  }

  @EventHandler
  public void onInventoryCloseEvent(InventoryCloseEvent event) {
    this.closeGUI(event.getPlayer(), false);
    PlayerUtils.removeDuplicateItems(event.getPlayer().getInventory());
  }

  @EventHandler
  public void onQuitEvent(PlayerQuitEvent event) {
    this.closeGUI(event.getPlayer(), true);
  }

  @EventHandler
  public void onTeleportEvent(PlayerTeleportEvent event) {
    this.closeGUI(event.getPlayer(), true);
  }
}
