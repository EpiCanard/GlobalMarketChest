package fr.epicanard.globalmarketchest.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.epicanard.globalmarketchest.GlobalMarketChest;

public class CloseGUICollector implements Listener {

  @EventHandler
  public void onInventoryCloseEvent(InventoryCloseEvent event) {
    if (event.getPlayer() != null && event.getPlayer() instanceof Player) {
      UUID playerID = event.getPlayer().getUniqueId();
      if (GlobalMarketChest.plugin.inventories.hasInventory(playerID)) {
        GlobalMarketChest.plugin.inventories.removeInventory(playerID);
      }
        
    }
  }
}
