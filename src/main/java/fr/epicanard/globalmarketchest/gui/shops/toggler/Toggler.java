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
  @Setter @Getter
  protected Boolean isSet = false;
  @Getter
  protected int pos;

  public Toggler(TogglerConfig config) {
    this.setItem = config.getSetItem();
    this.unsetItem = config.getUnsetItem();
    this.isSet = config.getSet();
    this.pos = config.getPosition();
  }

  /**
   * Load the toggler, Set or unset the element
   */
  public void load(Inventory inv) {
    this.setItemsView(inv);
  }

  /**
   * Change set boolean and load item in view
   */
  public void set(Inventory inv) {
    this.isSet = true;
    this.setItemsView(inv);
  }

  public void setAtPos(Inventory inv, Integer newPos) {
    this.pos = newPos;
    this.set(inv);
  }

  /**
   * Change unset boolean and load/unload item in view
   */
  public void unset(Inventory inv) {
    this.isSet = false;
    this.setItemsView(inv);
  }

  /**
   * Toggle the toggler, change from setItem to unsetItem or reverse
   */
  public void toggle(Inventory inv) {
    if (this.isSet)
      this.unset(inv);
    else
      this.set(inv);
  }

  /**
   * Set items in loaded view
   */
  public void setItemsView(Inventory inv) {
    Map<Integer, ItemStack> items = this.getItems(inv.getSize());
    items.forEach((position, itemstack) -> {
      inv.setItem(position, itemstack);
    });
  }

  public abstract Map<Integer, ItemStack> getItems(int inventorySize);

  public ItemStack getCurrentItem() {
    return (this.isSet) ? this.getSetItem() : this.getUnsetItem();
  }

  public ItemStack getOppositeItem() {
    return (!this.isSet) ? this.getSetItem() : this.getUnsetItem();
  }
}
