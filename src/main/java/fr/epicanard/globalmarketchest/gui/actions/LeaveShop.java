package fr.epicanard.globalmarketchest.gui.actions;

import java.util.function.Consumer;

import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;

/**
 * Consumer to leave the inventoryGUI
 */
public class LeaveShop implements Consumer<InventoryGUI> {

  @Override
  public void accept(InventoryGUI t) {
    t.unloadAllInterface();
    Player pl = t.getPlayer();
    t.close(pl);
    GlobalMarketChest.plugin.inventories.removeInventory(pl.getUniqueId());
  }
}