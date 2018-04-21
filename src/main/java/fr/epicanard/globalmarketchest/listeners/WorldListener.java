package fr.epicanard.globalmarketchest.listeners;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.shops.creation.GlobalChooseTypeShop;
import fr.epicanard.globalmarketchest.shops.ShopInfo;

public class WorldListener implements Listener {
  
  // TO DELETE
  public void TrashFunction(Location loc) {
    ShopInfo shop = GlobalMarketChest.plugin.shopManager.getShop(loc);
    
    if (shop != null) {
      System.out.println("================");
      System.out.println("ID :" + shop.getIDShop());
      Location sign = shop.getSignLocation();
      System.out.println("SignLocation :" + sign.getWorld().getName() + " - [" + sign.getX() + "," + sign.getY() + "," + sign.getZ() + "]");
      Location chest = shop.getChestLocation();
      System.out.println("ChestLocation :" + chest.getWorld().getName() + " - [" + chest.getX() + "," + chest.getY() + "," + chest.getZ() + "]");
      System.out.println("================");
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

      // TO DELETE
      this.TrashFunction(event.getClickedBlock().getLocation());
      if (bs instanceof Sign) {
        Sign sign = (Sign) bs;
        if (sign.getLine(0).equals("[GMC]")) {
          // GlobalMarketChest.plugin.inventories.addInventory(player.getUniqueId(), shopGUI);
        }
      }
    }
  }
}
