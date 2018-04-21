package fr.epicanard.globalmarketchest.gui.shops;

import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.gui.InterfacesLoader;
import lombok.Getter;
import lombok.experimental.Accessors;

public abstract class ShopInterface {
  @Accessors(fluent=true) @Getter
  protected Boolean isTemp = false;

  /**
   * Load specific interface with is className
   * 
   * @param gui
   */
  public void load(Inventory gui) {
    String className = this.getClass().getSimpleName();
    ItemStack[] items = InterfacesLoader.getInstance().getInterface(className);
    if (items == null)
      return;
    for (int i = 0; i < 54; i++)
      gui.setItem(i, items[i]);
    System.out.println(className);
  }

  /**
   * Unload interface
   */
  abstract public void unload();

  /**
   * Called when a mouse event is done inside inventory
   * 
   * @param event
   */
  abstract public void onClick(Event event);
  
}
