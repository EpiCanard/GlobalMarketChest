package fr.epicanard.globalmarketchest.gui.shops;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;

/**
 * Warning class used to make blinking item inside Inventory
 */
public class Warning {
  private Inventory inv;
  private int pos = -1;
  private BukkitTask task;
  private Boolean bool = false;
  private ItemStack[] warningItems = {null, null};
  private ItemStack old;

  public Warning(Inventory inv) {
    this.warningItems[0] = ItemStackUtils.getItemStackFromConfig("Interfaces.Warn");
    this.inv = inv;
  }

  /**
   * Set warning meta on the itemstack
   * 
   * @param message Message to set on the itemstack
   * @param item    ItemStack used to set metadata on
   */
  private ItemStack setWarn(String message, ItemStack item) {
    return ItemStackUtils.setItemStackMeta(item, 
      "/!\\ " + LangUtils.get("Divers.Warning"),
      "&7" + message);
  }

  /**
   * Make a blinking warning inside the inventory, create an infinite task
   * 
   * @param message Message to set on blinking warning
   * @param pos     Position where to place the warning
   */
  public void warn(String message, int pos) {
    String msg = LangUtils.get("ErrorMessages." + message);

    this.stopWarn();
    this.pos = pos;
    this.old = this.inv.getItem(pos);
    this.setWarn(msg, this.warningItems[0]);
    this.warningItems[1] = this.setWarn(msg, this.old.clone());
    this.task = Bukkit.getScheduler().runTaskTimer(GlobalMarketChest.plugin, this::run, 0, 10);
  }

  /**
   * Stop the blinking task
   */
  public void stopWarn() {
    if (this.task != null && !this.task.isCancelled())
      this.task.cancel();
    this.bool = false;
    if (this.pos >= 0)
      this.inv.setItem(this.pos, this.old);
    this.warningItems[1] = null;
  }

  /**
   * Runnable used on each task loop
   * Alternate the item set
   */
  private void run() {
    if (this.inv == null) {
      this.stopWarn();
      return;
    }

    this.bool = !this.bool;
    this.inv.setItem(this.pos, (this.bool) ? this.warningItems[0] : this.warningItems[1]);
  }
}