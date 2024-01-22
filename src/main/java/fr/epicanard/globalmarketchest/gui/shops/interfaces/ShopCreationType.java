package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.ChatInput;
import fr.epicanard.globalmarketchest.gui.actions.InterfaceType;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.ShopInterface;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopType;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import org.bukkit.inventory.ItemStack;

public class ShopCreationType extends ShopInterface {

  public ShopCreationType(InventoryGUI inv) {
    super(inv);

    if (Permissions.AS_CREATESHOP.isSetOn(inv.getPlayer()))
      this.togglerManager.setTogglerWithAction(inv.getInv(), 29, this.actions, i -> this.selectShopType(ShopType.ADMINSHOP));
    if (Permissions.GS_CREATESHOP.isSetOn(inv.getPlayer()))
      this.togglerManager.setTogglerWithAction(inv.getInv(), 33, this.actions, i -> this.selectShopType(ShopType.GLOBALSHOP));
    this.actions.put(48, new NextInterface(InterfaceType.SHOP_CREATION_ATTACH_BLOCK));
    this.actions.put(50, new ChatInput("InfoMessages.WriteGroupName", this::changeName));
  }

  @Override
  public void load() {
    super.load();
    this.updateName();
    this.updateAttachedBlock();
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

  /**
   * Update the item with the name of the current shop
   */
  private void updateName() {
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    ItemStack item = this.inv.getInv().getItem(50);

    item = ItemStackUtils.setItemStackDisplayName(item, ShopUtils.generateKeyValue(LangUtils.get("Divers.Name"), shop.getGroup()));
    this.inv.getInv().setItem(50, item);
  }

  private void updateAttachedBlock() {
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    ItemStack item = this.inv.getInv().getItem(48);

    item = ItemStackUtils.setItemStackDisplayName(item, ShopUtils.generateKeyValue(LangUtils.get("Divers.AttachedBlock"), shop.getRawOtherLocation()));
    this.inv.getInv().setItem(48, item);
  }
}
