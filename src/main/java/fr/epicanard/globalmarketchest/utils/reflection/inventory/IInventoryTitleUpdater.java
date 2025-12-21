package fr.epicanard.globalmarketchest.utils.reflection.inventory;

import org.bukkit.entity.Player;

public interface IInventoryTitleUpdater {

  /**
   * Update the current inventory name
   *
   * @param player Player owner of inventory
   * @param title New title of inventory
   */
  void updateInventoryName(Player player, String title);
}
