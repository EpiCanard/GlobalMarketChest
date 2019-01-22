package fr.epicanard.globalmarketchest.gui.shops.toggler;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class CircleToggler extends Toggler{
  public CircleToggler(Inventory inv, TogglerConfig config) {
    super(inv, config);
    ItemStackUtils.setItemStackMeta(this.setItem, null, null);
    ItemStackUtils.setItemStackMeta(this.unsetItem, null, null);
  }

  public CircleToggler(Inventory inv, Integer pos, ItemStack setItem, ItemStack unsetItem) {
    super(inv, pos, setItem, unsetItem);
  }

  public CircleToggler(Inventory inv, Integer pos) {
    super(inv, pos, ItemStackUtils.getItemStackFromConfig("Interfaces.Circle.SetItem"), ItemStackUtils.getItemStackFromConfig("Interfaces.Circle.UnsetItem"));
    ItemStackUtils.setItemStackMeta(this.setItem, null, null);
    ItemStackUtils.setItemStackMeta(this.unsetItem, null, null);
 }

  /**
   * Fill the circle with item
   *
   * @param item Item to set on circle
   */
  private void setCircle(ItemStack item) {
    int start = this.pos - 10;
    for (int i = 0; i < 9; i++) {
      int pos = (start + i % 3) + Utils.getLine(i, 3) * 9;
      if (pos > 0 && pos < this.inv.getSize() && i != 4)
        this.inv.setItem(pos, item);
    }
  }

  /**
   * Fill the circle with setItem
   */
  public void setInView() {
    this.setCircle(this.setItem);
  }

  /**
   * Fill the circle with unsetItem
   */
  public void unsetInView() {
    this.setCircle(this.unsetItem);
  }
}