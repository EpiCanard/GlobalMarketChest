package fr.epicanard.globalmarketchest.gui.actions;

import java.util.function.Consumer;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;

public class NextInterface implements Consumer<InventoryGUI> {
  private String name;

  public NextInterface(String name) {
    this.name = name;
  }

  @Override
  public void accept(InventoryGUI t) {
    t.loadInterface(name);
  }
}