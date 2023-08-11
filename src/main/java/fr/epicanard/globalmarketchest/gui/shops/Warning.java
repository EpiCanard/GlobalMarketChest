package fr.epicanard.globalmarketchest.gui.shops;

import fr.epicanard.globalmarketchest.executor.BaseExecutor;
import fr.epicanard.globalmarketchest.executor.Task;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Warning class used to make blinking item inside Inventory
 */
public class Warning {
  private BaseExecutor executor;
  private Inventory inv;
  private int pos = -1;
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
      Utils.toList("&7" + message));
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
    executor = Utils.getExecutor();
    executor.task(new Task(this::run), 10, 10);
  }

  /**
   * Stop the blinking task
   */
  public void stopWarn() {
    if (this.executor != null)
      this.executor.shutdown();
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
