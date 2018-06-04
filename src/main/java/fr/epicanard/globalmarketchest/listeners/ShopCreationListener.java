package fr.epicanard.globalmarketchest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopType;
import fr.epicanard.globalmarketchest.utils.ShopUtils;

/**
 * Listener for creation process
 */
public class ShopCreationListener implements Listener {
  
  @EventHandler
  public void onChangeSign(SignChangeEvent event) {    
    Player player = event.getPlayer();
    
    if (event.getLine(0).compareTo("[GMC]") == 0) {
      ShopInfo shop = new ShopInfo(-1, player.getUniqueId().toString(), ShopType.GLOBALSHOP.setOn(0), event.getBlock().getLocation(), null, ShopUtils.generateName());
      InventoryGUI inv = new InventoryGUI();

      inv.getTransaction().put(TransactionKey.SHOPINFO, shop);
      GlobalMarketChest.plugin.inventories.addInventory(player.getUniqueId(), inv);
      inv.loadInterface("ShopCreationSelectType");
      inv.open(player);
    }
  }
}
