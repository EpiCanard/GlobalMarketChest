package fr.epicanard.globalmarketchest.gui.shops;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface Droppable {
  /**
   * Called when a mouse drop event is done inside inventory
   *
   * @param event
   */
  void onDrop(InventoryClickEvent event, InventoryGUI inv);
}
