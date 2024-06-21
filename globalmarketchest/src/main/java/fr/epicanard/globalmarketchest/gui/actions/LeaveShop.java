package fr.epicanard.globalmarketchest.gui.actions;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Consumer to leave the inventoryGUI
 */
public class LeaveShop implements Consumer<InventoryGUI> {

  @Override
  public void accept(InventoryGUI t) {
    t.unloadAllInterface();
    Player pl = t.getPlayer();
    t.close();
    GlobalMarketChest.plugin.inventories.removeInventory(pl.getUniqueId());
  }
}
