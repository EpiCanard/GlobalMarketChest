package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.InterfaceType;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.ShopCreationInterface;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopType;

public class ShopCreationType extends ShopCreationInterface {

  public ShopCreationType(InventoryGUI inv) {
    super(inv);

    if (Permissions.AS_CREATESHOP.isSetOn(inv.getPlayer()))
      this.togglerManager.setTogglerWithAction(inv.getInv(), 29, this.actions, i -> this.selectShopType(ShopType.ADMINSHOP));
    if (Permissions.GS_CREATESHOP.isSetOn(inv.getPlayer()))
      this.togglerManager.setTogglerWithAction(inv.getInv(), 33, this.actions, i -> this.selectShopType(ShopType.GLOBALSHOP));
  }

  @Override
  public void load() {
    super.load();
    this.updateName();
    this.updateAttachedBlock();
  }

  private void selectShopType(ShopType type) {
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    shop.setType(type);
    this.inv.loadInterface(InterfaceType.SHOP_CREATION_MODE);
  }

}
