package fr.epicanard.globalmarketchest.gui.shops;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.utils.Utils;

public class Toggler {
  private ItemStack old;
  private Boolean set = true;
  private Inventory inv;
  private int pos;

  public Toggler(Inventory inv, int pos) {
    this.inv = inv;
    this.pos = pos;
  }

  public void load() {
    this.old = inv.getItem(pos);
  }

  public void set() {
    this.inv.setItem(this.pos, this.old);
    this.set = true;
  }

  public void unset() {
    this.inv.setItem(this.pos, Utils.getBackground());
    this.set = false;
  }

  public void toggle() {
    if (this.set == true)
      this.unset();
    else
      this.set();
  }
}