package fr.epicanard.globalmarketchest.gui.shops.baseinterfaces;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.ChatInput;
import fr.epicanard.globalmarketchest.gui.actions.InterfaceType;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import org.bukkit.inventory.ItemStack;

public abstract class ShopCreationInterface extends ShopInterface {

  public ShopCreationInterface(InventoryGUI inv) {
    super(inv);
    this.actions.put(48, new NextInterface(InterfaceType.SHOP_CREATION_ATTACH_BLOCK));
    this.actions.put(50, new ChatInput("InfoMessages.WriteGroupName", this::changeName));
  }

  // TODO What to do if we have global and admin shop with same name ?
  private void changeName(String name) {
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    shop.setGroup(name);
    this.inv.reloadLastInterface();
  }

  protected void updateAttachedBlock() {
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    ItemStack item = this.inv.getInv().getItem(48);

    item = ItemStackUtils.setItemStackDisplayName(item, ShopUtils.generateKeyValue(LangUtils.get("Divers.AttachedBlock"), shop.getRawOtherLocation()));
    this.inv.getInv().setItem(48, item);
  }

  /**
   * Update the item with the name of the current shop
   */
  protected void updateName() {
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    if (shop == null)
      return;

    ItemStack item = this.inv.getInv().getItem(50);
    item = ItemStackUtils.setItemStackDisplayName(item, ShopUtils.generateKeyValue(LangUtils.get("Divers.Name"), shop.getGroup()));
    this.inv.getInv().setItem(50, item);
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
