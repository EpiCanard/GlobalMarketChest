package fr.epicanard.globalmarketchest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.ShopAlreadyExistException;
import fr.epicanard.globalmarketchest.shops.ShopType;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

/**
 * Listener for creation process
 */
public class ShopCreationListener implements Listener {
  
  @EventHandler
  public void onChangeSign(SignChangeEvent event) {    
    Player player = event.getPlayer();
    
    try {
      if (event.getLine(0).compareTo("[GMC]") == 0)
        GlobalMarketChest.plugin.shopManager.createShop(player, event.getBlock().getLocation(), null, ShopType.GLOBALSHOP.setOn(0), "MyWorld");
    } catch (ShopAlreadyExistException e) {
      PlayerUtils.sendMessagePlayer(player, e.getMessage());
      event.getBlock().breakNaturally();
    }
  }
}
