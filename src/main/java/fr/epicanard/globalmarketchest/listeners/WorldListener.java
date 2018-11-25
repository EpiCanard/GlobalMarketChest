package fr.epicanard.globalmarketchest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;

/**
 * Listener for every world interact like opennin a chest
 */
public class WorldListener implements Listener {

  @EventHandler
  public void onPlayerBreakBlock(BlockBreakEvent event) {
    if (event.getBlock().hasMetadata(ShopUtils.META_KEY)) {
      ShopInfo shop = ShopUtils.getShop(event.getBlock());
      if (GlobalMarketChest.plugin.shopManager.deleteShop(shop)) {
        PlayerUtils.sendMessageConfig(event.getPlayer(), "InfoMessages.ShopDeleted");
        String owner = shop.getOwner();
        LoggerUtils.info(String.format("%s : [%s:%s<%s>]", LangUtils.get("InfoMessages.ShopDeleted"),
          shop.getSignLocation().toString(), PlayerUtils.getPlayerName(owner), owner));
      }

    }
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
      if (!Permissions.GS_OPENSHOP.isSetOnWithMessage(player)) {
        return;
      }
      if (GlobalMarketChest.plugin.inventories.hasInventory(player.getUniqueId())) {
        InventoryGUI old = GlobalMarketChest.plugin.inventories.removeInventory(player.getUniqueId());
        old.close();
      }
      InventoryGUI inv = new InventoryGUI(player);
      GlobalMarketChest.plugin.inventories.addInventory(player.getUniqueId(), inv);
      inv.getTransaction().put(TransactionKey.SHOPINFO, shop);
      inv.open();
      inv.loadInterface("CategoryView");
    }
  }
}
