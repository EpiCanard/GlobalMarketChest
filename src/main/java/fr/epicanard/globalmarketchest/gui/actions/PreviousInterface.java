package fr.epicanard.globalmarketchest.gui.actions;

import java.util.function.Consumer;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;

/**
 * Consumer to unload the last interface
 */
public class PreviousInterface implements Consumer<InventoryGUI> {
  @Override
  public void accept(InventoryGUI t) {
    t.unloadLastInterface();
  }
}