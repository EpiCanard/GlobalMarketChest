package fr.epicanard.globalmarketchest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.ShopInterface;

/**
 * Listener for each interaction done inside a chest
 */
public class GUIListener implements Listener {

  @EventHandler
  public void onClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player))
      return;

    InventoryGUI inv = GlobalMarketChest.plugin.inventories.getInventory(event.getWhoClicked().getUniqueId());

    if (inv == null || !inv.inventoryEquals(event.getInventory()))
      return;

    if (event.isShiftClick() || event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.DROP)
      event.setCancelled(true);

    ShopInterface interf = inv.getInterface();

    if (inv.inventoryEquals(event.getClickedInventory())) {
      event.setCancelled(true);
      if (event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_ONE || event.getAction() == InventoryAction.SWAP_WITH_CURSOR)
        interf.onDrop(event, inv);
      else
        interf.onClick(event, inv);
      return;
    }

    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
      event.setCancelled(true);
      interf.onDrop(event, inv);
    }
  }

  @EventHandler
  public void onDrag(InventoryDragEvent event) {
    if (event.getWhoClicked() instanceof Player) {
      Player p = (Player)event.getWhoClicked();
      InventoryGUI inv = GlobalMarketChest.plugin.inventories.getInventory(p.getUniqueId());
      if (inv != null && inv.inventoryEquals(event.getInventory()))
        event.setCancelled(true);
    }
  }

}
