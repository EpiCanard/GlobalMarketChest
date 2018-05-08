package fr.epicanard.globalmarketchest.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

/**
 * Listener for every world interact like opennin a chest
 */
public class WorldListener implements Listener {

  @EventHandler
  public void onPlayerBreakBlock(BlockBreakEvent event) {
    Material type = event.getBlock().getType();
    if (type.equals(Material.SIGN_POST) || type.equals(Material.WALL_SIGN)) {
      if (GlobalMarketChest.plugin.shopManager.deleteShop(event.getBlock().getLocation()))
        PlayerUtils.sendMessagePlayer(event.getPlayer(), "Shop successfully deleted");
    }

  }
  
  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Action action = event.getAction();

    if (event.getClickedBlock() != null && action == Action.RIGHT_CLICK_BLOCK) {
      BlockState bs = event.getClickedBlock().getState();
      ShopInfo shop = GlobalMarketChest.plugin.shopManager.getShop(event.getClickedBlock().getLocation());
      
      if (shop != null && (bs instanceof Sign || bs instanceof EnderChest || bs instanceof Chest)) {
        // Create shop interface for player
      }
    }
  }
}
