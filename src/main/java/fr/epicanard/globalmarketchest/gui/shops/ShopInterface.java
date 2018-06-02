package fr.epicanard.globalmarketchest.gui.shops;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.gui.InterfacesLoader;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.paginator.Paginator;
import fr.epicanard.globalmarketchest.gui.paginator.PaginatorConfig;
import fr.epicanard.globalmarketchest.gui.actions.LeaveShop;
import lombok.Getter;
import lombok.experimental.Accessors;

public abstract class ShopInterface {
  @Accessors(fluent=true) @Getter
  protected Boolean isTemp = false;
  protected Paginator paginator = null;
  protected InventoryGUI inv;
  protected Map<Integer, Consumer<InventoryGUI>> actions = new HashMap<Integer, Consumer<InventoryGUI>>();

  public ShopInterface(InventoryGUI inv) {
    this.inv = inv;
    String className = this.getClass().getSimpleName();
    PaginatorConfig conf = InterfacesLoader.getInstance().getPaginatorConfig(className);
    if (conf != null)
      this.paginator = new Paginator(this.inv.getInv(), conf);
    this.actions.put(8, new LeaveShop());
  }

  /**
   * Load specific interface with is className
   * 
   * @param gui
   */
  public void load() {
    String className = this.getClass().getSimpleName();
    ItemStack[] items = InterfacesLoader.getInstance().getInterface(className);
    if (items == null)
      return;
    for (int i = 0; i < 54; i++)
      this.inv.getInv().setItem(i, items[i]);
    if (this.paginator != null)
      this.paginator.reloadInterface();
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
  public void onClick(InventoryClickEvent event, InventoryGUI inv) {
    if (event.getClick() != ClickType.LEFT)
      return;
    if (this.paginator == null ||!this.paginator.onClick(event.getSlot()))
      Optional.ofNullable(this.actions.get(event.getSlot())).ifPresent(c -> c.accept(inv));
  }
  
}
