package fr.epicanard.globalmarketchest.gui.shops.toggler;

import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CircleToggler extends Toggler {
  public CircleToggler(Inventory inv, TogglerConfig config) {
    super(inv, config);
    ItemStackUtils.setItemStackMeta(this.setItem, null, null);
    ItemStackUtils.setItemStackMeta(this.unsetItem, null, null);
  }

  /**
   * Get items with their position to use in interface
   *
   * @return map of itemstack with their position
   */
  public Map<Integer, ItemStack> getItems() {
    Map<Integer, ItemStack> items = new HashMap<>();
    ItemStack item = (this.isSet) ? this.setItem : this.unsetItem;
    int start = this.pos - 10;
    for (int i = 0; i < 9; i++) {
      int pos = (start + i % 3) + Utils.getLine(i, 3) * 9;
      if (pos > 0 && pos < this.inv.getSize() && i != 4)
        items.put(pos, item);
    }
    return items;
  }
}
