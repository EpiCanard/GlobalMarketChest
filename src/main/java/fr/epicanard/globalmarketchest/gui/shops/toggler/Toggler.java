package fr.epicanard.globalmarketchest.gui.shops.toggler;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Setter;

public abstract class Toggler {
  @Setter
  protected ItemStack setItem;
  @Setter
  protected ItemStack unsetItem;
  @Setter
  private Boolean set = false;
  protected Inventory inv;
  protected int pos;

  public Toggler(Inventory inv, int pos, ItemStack setItem, ItemStack unsetItem) {
    this.inv = inv;
    this.pos = pos;
    this.setItem = setItem;
    this.unsetItem = unsetItem;
  }

  public void load() {
    if (set)
      this.setInView();
    else
      this.unsetInView();
  }

  public void set() {
    this.setInView();
    this.set = true;
  }

  public void unset() {
    this.unsetInView();
    this.set = false;
  }

  public void toggle() {
    if (this.set == true)
      this.unset();
    else
      this.set();
  }

  protected abstract void setInView();
  protected abstract void unsetInView();
}