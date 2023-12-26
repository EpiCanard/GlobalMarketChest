package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.ChatInput;
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
    this.actions.put(49, new ChatInput("InfoMessages.WriteGroupName", this::changeName));
  }

  // TODO What to do if we have global and admin shop with same name ?
  public void changeName(String name) {
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    shop.setGroup(name);
    this.inv.reloadLastInterface();
  }

  private void selectShopType(ShopType type) {
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    shop.setType(type);
  }
}
