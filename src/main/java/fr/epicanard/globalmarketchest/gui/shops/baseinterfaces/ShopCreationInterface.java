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
   * Update the lore of the current shop
   */
  protected void updateName() {
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    ItemStack item = this.inv.getInv().getItem(49);

    if (shop == null)
      return;
    item = ItemStackUtils.setItemStackMeta(item, LangUtils.get("Shops.CurrentShop"), ShopUtils.generateLoreWithOther(shop));
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
