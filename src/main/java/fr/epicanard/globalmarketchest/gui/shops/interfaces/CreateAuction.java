package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.gui.InterfacesLoader;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.gui.shops.Toggler;

public class CreateAuction extends ShopInterface {
  private Toggler toggler;

  public CreateAuction(InventoryGUI inv) {
    super(inv);
    this.toggler = new Toggler(inv.getInv(), 53);
    this.actions.put(22, i -> this.unsetItem());
    this.actions.put(0, new PreviousInterface(this::unsetItem));
    this.actions.put(53, new NextInterface("CreateAuctionPrice", this::checkItem));
  }

  @Override
  public void load() {
    super.load();
    this.toggler.load();

    ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMPITEM);
    if (item != null)
      this.inv.getInv().setItem(22, item);
    else
      this.toggler.unset();
  }

  @Override
  public void unload() {
  }

  /**
   * Set the item in dropzone when drop
   * 
   * @param item ItemStack to set in drop zone
   */
  private void setItem(ItemStack item) {
    this.inv.getTransaction().put(TransactionKey.TEMPITEM, item);
    this.inv.getInv().setItem(22, item);
    this.toggler.set();
  }

  /**
   * Remove the item from drop zone
   */
  private void unsetItem() {
    this.inv.getTransaction().remove(TransactionKey.TEMPITEM);
    ItemStack[] items = InterfacesLoader.getInstance().getInterface("CreateAuction");
    this.inv.getInv().setItem(22, items[22]);
    this.toggler.unset();
  }

  /**
   * Check if TEMPITEM is set (item dropped in interface)
   * 
   * @return false if TEMPITEM is not set else true
   */
  private Boolean checkItem() {
    return (this.inv.getTransactionValue(TransactionKey.TEMPITEM) != null);
  }

  /**
   * Called when a mouse drop event is done inside inventory
   * 
   * @param event
   */
  public void onDrop(InventoryClickEvent event, InventoryGUI inv) {
    ItemStack item;
    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
      item = event.getCurrentItem();
    else
      item = event.getCursor();
    this.setItem(item);
  }  
}
