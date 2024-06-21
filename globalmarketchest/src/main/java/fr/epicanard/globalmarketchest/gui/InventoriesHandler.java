package fr.epicanard.globalmarketchest.gui;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Store each opened shop interface by player (uuid)
 */
public class InventoriesHandler {
  @Getter
  private Map<UUID, InventoryGUI> inventories;

  public InventoriesHandler() {
    this.inventories = new HashMap<UUID, InventoryGUI>();
  }

  public void addInventory(UUID playerID, InventoryGUI inv) {
    this.inventories.put(playerID, inv);
  }

  public InventoryGUI removeInventory(UUID playerID) {
    InventoryGUI inv = this.inventories.remove(playerID);
    if (inv != null)
      inv.destroy();
    return inv;
  }

  public void removeAllInventories() {
    for (InventoryGUI i : this.inventories.values())
      i.destroy();
    this.inventories.clear();
  }

  public Boolean hasInventory(UUID playerID) {
    return this.inventories.containsKey(playerID);
  }

  public InventoryGUI getInventory(UUID playerID) {
    return this.inventories.get(playerID);
  }
}
