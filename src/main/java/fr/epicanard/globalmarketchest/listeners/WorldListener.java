package fr.epicanard.globalmarketchest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;

/**
 * Listener for every world interact like opennin a chest
 */
public class WorldListener implements Listener {

  @EventHandler
  public void onPlayerBreakBlock(BlockBreakEvent event) {
    if (event.getBlock().hasMetadata(ShopUtils.META_KEY))
      if (GlobalMarketChest.plugin.shopManager.deleteShop(ShopUtils.getShop(event.getBlock())))
        PlayerUtils.sendMessagePlayer(event.getPlayer(), "Shop successfully deleted");
  }
  
  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Action action = event.getAction();

    if (event.getClickedBlock() != null && action == Action.RIGHT_CLICK_BLOCK) {
      ShopInfo shop = ShopUtils.getShop(event.getClickedBlock());

      if (shop == null)
        return;
      event.setCancelled(true);
      if (GlobalMarketChest.plugin.inventories.hasInventory(player.getUniqueId())) {
        InventoryGUI old = GlobalMarketChest.plugin.inventories.removeInventory(player.getUniqueId());
        old.close(player);
      }
      InventoryGUI inv = new InventoryGUI();
      GlobalMarketChest.plugin.inventories.addInventory(player.getUniqueId(), inv);
      inv.loadInterface("GlobalShop");
      inv.open(player);
    }
  }
}
