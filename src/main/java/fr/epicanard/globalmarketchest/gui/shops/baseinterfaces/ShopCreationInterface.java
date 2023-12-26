package fr.epicanard.globalmarketchest.gui.shops.baseinterfaces;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import org.bukkit.inventory.ItemStack;

public abstract class ShopCreationInterface extends ShopInterface {

  public ShopCreationInterface(InventoryGUI inv) {
    super(inv);
  }

  /**
   * Update the item with the name of the current shop
   */
  protected void updateName() {
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    if (shop == null)
      return;

    ItemStack item = this.inv.getInv().getItem(49);
    item = ItemStackUtils.setItemStackDisplayName(item, ShopUtils.generateKeyValue(LangUtils.get("Divers.Name"), shop.getGroup()));
    this.inv.getInv().setItem(49, item);
  }

  /**
   * Called when loading the interface
   */
  @Override
  public void load() {
    super.load();
    this.updateName();
  }

}
