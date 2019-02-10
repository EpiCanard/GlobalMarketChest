package fr.epicanard.globalmarketchest.gui.actions;

import java.util.function.Consumer;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;

/**
 * Consumer to leave the inventoryGUI
 */
public class ReturnBack implements Consumer<InventoryGUI> {
  private Runnable runnable;

  public ReturnBack() {}

  public ReturnBack(Runnable runnable) {
    this.runnable = runnable;
  }

  @Override
  public void accept(InventoryGUI t) {
    if (this.runnable != null)
      this.runnable.run();
    t.unloadTempInterface();
  }

  public static void execute(Runnable runnable, InventoryGUI gui) {
    ReturnBack ret = new ReturnBack(runnable);
    ret.accept(gui);
  }
}