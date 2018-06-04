package fr.epicanard.globalmarketchest.gui.actions;

import java.util.function.Consumer;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;

/**
 * Consumer to unload the last interface
 */
public class PreviousInterface implements Consumer<InventoryGUI> {
  private Runnable runnable = () -> {};

  public PreviousInterface() {}

  public PreviousInterface(Runnable runnable) {
    if (runnable != null)
      this.runnable = runnable;
  }

  @Override
  public void accept(InventoryGUI t) {
    this.runnable.run();
    t.unloadLastInterface();
  }
}