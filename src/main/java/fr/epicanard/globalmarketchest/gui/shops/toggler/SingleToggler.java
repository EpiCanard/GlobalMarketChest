package fr.epicanard.globalmarketchest.gui.shops.toggler;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SingleToggler extends Toggler{
  public SingleToggler(Inventory inv, int pos, ItemStack setItem, ItemStack unsetItem) {
    super(inv, pos, setItem, unsetItem);
  }

  public SingleToggler(Inventory inv, int pos, ItemStack setItem, ItemStack unsetItem, Boolean set) {
    super(inv, pos, setItem, unsetItem);
    this.setIsSet(set);
  }

  public void setInView() {
    this.inv.setItem(this.pos, this.setItem);
  }

  public void unsetInView() {
    this.inv.setItem(this.pos, this.unsetItem);
  }
}