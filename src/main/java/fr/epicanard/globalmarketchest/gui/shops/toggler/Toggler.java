package fr.epicanard.globalmarketchest.gui.shops.toggler;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class Toggler {
  @Setter @Getter
  protected ItemStack setItem;
  @Setter @Getter
  protected ItemStack unsetItem;
  @Setter
  protected Boolean isSet = false;
  protected Inventory inv;
  protected int pos;

  public Toggler(Inventory inv, TogglerConfig config) {
    this.inv = inv;
    this.setItem = config.getSetItem();
    this.unsetItem = config.getUnsetItem();
    this.isSet = config.getSet();
    this.pos = config.getPosition();
  }

  /**
   * Load the toggler, Set or unset the element
   */
  public void load() {
    this.setItemsView();
  }

  /**
   * Change set boolean and load item in view
   */
  public void set() {
    this.isSet = true;
    this.setItemsView();
  }

  /**
   * Change unset boolean and load/unload item in view
   */
  public void unset() {
    this.isSet = false;
    this.setItemsView();
  }

  /**
   * Toggle the toggler, change from setItem to unsetItem or reverse
   */
  public void toggle() {
    if (this.isSet)
      this.unset();
    else
      this.set();
  }

  /**
   * Set items in loaded view
   */
  public void setItemsView() {
    Map<Integer, ItemStack> items = this.getItems();
    items.forEach((position, itemstack) -> {
      this.inv.setItem(position, itemstack);
    });
  }

  public abstract Map<Integer, ItemStack> getItems();

  public ItemStack getCurrentItem() {
    return (this.isSet) ? this.getSetItem() : this.getUnsetItem();
  }

  public ItemStack getOppositeItem() {
    return (!this.isSet) ? this.getSetItem() : this.getUnsetItem();
  }
}
