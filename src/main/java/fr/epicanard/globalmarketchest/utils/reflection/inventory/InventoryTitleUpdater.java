package fr.epicanard.globalmarketchest.utils.reflection.inventory;

import org.bukkit.entity.Player;

public class InventoryTitleUpdater implements IInventoryTitleUpdater {

  /**
   * Update the current inventory name
   *
   * @param player Player owner of inventory
   * @param title New title of inventory
   */
  @Override
  public void updateInventoryName(Player player, String title) {
    player.getOpenInventory().setTitle(title);
  }
}
