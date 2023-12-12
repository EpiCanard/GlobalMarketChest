package fr.epicanard.globalmarketchest.gui.shops.toggler;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SingleToggler extends Toggler {
  public SingleToggler(TogglerConfig config) {
    super(config);
  }

  /**
   * Get items with their position to use in interface
   *
   * @return map of itemstack with their position
   */
  public Map<Integer, ItemStack> getItems(int inventorySize) {
    Map<Integer, ItemStack> items = new HashMap<>();
    items.put(this.pos, (this.isSet) ? this.setItem : this.unsetItem);
    return items;
  }
}
