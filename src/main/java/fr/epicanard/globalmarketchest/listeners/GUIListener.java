package fr.epicanard.globalmarketchest.listeners;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.shops.Droppable;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.ShopInterface;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Listener for each interaction done inside a chest
 */
public class GUIListener implements Listener {

  @EventHandler
  public void onClick(final InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player))
      return;

    final InventoryGUI inv = GlobalMarketChest.plugin.inventories.getInventory(event.getWhoClicked().getUniqueId());

    if (inv == null || !inv.inventoryEquals(event.getInventory()))
      return;

    if (event.isShiftClick() || event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.DROP)
      event.setCancelled(true);

    final ShopInterface interf = inv.getInterface();

    if (inv.inventoryEquals(event.getClickedInventory())) {
      event.setCancelled(true);
      if (
          event.getAction() == InventoryAction.PLACE_ALL
          || event.getAction() == InventoryAction.PLACE_ONE
          || event.getAction() == InventoryAction.SWAP_WITH_CURSOR
      ) {
        if (interf instanceof Droppable) {
          ((Droppable) interf).onDrop(event, inv);
        }
      } else {
        interf.onClick(event, inv);
      }
      return;
    }

    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
      event.setCancelled(true);
      if (interf instanceof Droppable) {
        ((Droppable) interf).onDrop(event, inv);
      }
    }
  }

  @EventHandler
  public void onDrag(final InventoryDragEvent event) {
    if (event.getWhoClicked() instanceof Player) {
      final Player p = (Player) event.getWhoClicked();
      final InventoryGUI inv = GlobalMarketChest.plugin.inventories.getInventory(p.getUniqueId());
      if (inv != null && inv.inventoryEquals(event.getInventory()))
        event.setCancelled(true);
    }
  }

  /**
   * Block the pickup of item if the interface is of type droppable
   *
   * @param event Pickup event
   */
  @EventHandler
  public void onPickupItem(final EntityPickupItemEvent event) {
    final Entity entity = event.getEntity();
    if (entity instanceof Player) {
      final InventoryGUI inv = GlobalMarketChest.plugin.inventories.getInventory(entity.getUniqueId());

      if (inv != null && inv.getInterface() instanceof Droppable) {
        event.setCancelled(true);
      }
    }
  }

}
