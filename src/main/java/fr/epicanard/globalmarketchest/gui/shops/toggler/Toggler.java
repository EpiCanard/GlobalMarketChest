package fr.epicanard.globalmarketchest.gui.shops.toggler;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

public abstract class Toggler {
  @Setter @Getter
  protected ItemStack setItem;
  @Setter @Getter
  protected ItemStack unsetItem;
  @Setter
  private Boolean isSet = false;
  protected Inventory inv;
  protected int pos;

  public Toggler(Inventory inv, int pos, ItemStack setItem, ItemStack unsetItem) {
    this.inv = inv;
    this.pos = pos;
    this.setItem = setItem;
    this.unsetItem = unsetItem;
  }

  /**
   * Load the toggler, Set or unset the element
   */
  public void load() {
    if (this.isSet)
      this.setInView();
    else
      this.unsetInView();
  }

  /**
   * Change set boolean and load item in view
   */
  public void set() {
    this.setInView();
    this.isSet = true;
  }

  /**
   * Change unset boolean and load/unload item in view
   */
  public void unset() {
    this.unsetInView();
    this.isSet = false;
  }

  public void toggle() {
    if (this.isSet)
      this.unset();
    else
      this.set();
  }

  protected abstract void setInView();
  protected abstract void unsetInView();
}