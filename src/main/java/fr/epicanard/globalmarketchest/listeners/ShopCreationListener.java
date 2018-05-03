package fr.epicanard.globalmarketchest.listeners;

import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import fr.epicanard.globalmarketchest.permissions.Permissions;

/**
 * Listener for creation process
 */
public class ShopCreationListener implements Listener {
  
  @EventHandler
  public void onBlockPlaceEvent(BlockPlaceEvent event) {    
    Player player = event.getPlayer();
    
    BlockState bs = event.getBlockPlaced().getState();
    if (bs instanceof Chest && Permissions.LOCALSHOP_CREATE.isSetOn(player, event.getBlockPlaced().getWorld())) {
      // Create local shop
    }
  }
}
