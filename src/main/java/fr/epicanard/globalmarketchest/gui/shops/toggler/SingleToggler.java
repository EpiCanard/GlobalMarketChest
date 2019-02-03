package fr.epicanard.globalmarketchest.gui.shops.toggler;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SingleToggler extends Toggler{
  public SingleToggler(Inventory inv, TogglerConfig config) {
    super(inv, config);
  }

  public SingleToggler(Inventory inv, int pos, ItemStack setItem, ItemStack unsetItem) {
    super(inv, pos, setItem, unsetItem);
  }

  public SingleToggler(Inventory inv, int pos, ItemStack setItem, ItemStack unsetItem, Boolean set) {
    super(inv, pos, setItem, unsetItem);
    this.setIsSet(set);
  }

  /**
   * Get items with their position to use in interface
   *
   * @return map of itemstack with their position
   */
  public Map<Integer, ItemStack> getItems() {
    Map<Integer, ItemStack> items = new HashMap<>();
    items.put(this.pos, (this.isSet) ? this.setItem : this.unsetItem);
    return items;
  }
}