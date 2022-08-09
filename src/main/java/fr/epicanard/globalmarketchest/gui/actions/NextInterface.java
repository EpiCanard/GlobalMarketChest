package fr.epicanard.globalmarketchest.gui.actions;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Consumer to load the specified interface
 * 
 * Can be build with a Callable<Boolean> to make blocking verification before loading the interface
 */
public class NextInterface implements Consumer<InventoryGUI> {
  private String name;
  private Callable<Boolean> callable;

  public NextInterface(String name) {
    this.name = name;
    this.callable = () -> true;
  }

  public NextInterface(String name, Callable<Boolean> callable) {
    this.name = name;
    this.callable = callable;
  }

  @Override
  public void accept(InventoryGUI t) {
    try {
      if (this.callable == null || this.callable.call())
        t.loadInterface(name);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
