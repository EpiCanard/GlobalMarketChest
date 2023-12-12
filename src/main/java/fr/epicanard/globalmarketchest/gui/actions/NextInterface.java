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
  private InterfaceType interfaceType;
  private Callable<Boolean> callable;

  public NextInterface(InterfaceType interfaceType) {
    this.interfaceType = interfaceType;
    this.callable = () -> true;
  }

  public NextInterface(InterfaceType interfaceType, Callable<Boolean> callable) {
    this.interfaceType = interfaceType;
    this.callable = callable;
  }

  @Override
  public void accept(InventoryGUI t) {
    try {
      if (this.callable == null || this.callable.call())
        t.loadInterface(interfaceType);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
