package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.ShopAlreadyExistException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.LeaveShop;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.ShopCreationInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopMode;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

import java.util.function.Consumer;

public class ShopCreationMode extends ShopCreationInterface {

  public ShopCreationMode(InventoryGUI inv) {
    super(inv);
    ShopInfo shopInfo = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    switch (shopInfo.getType()) {
      case ADMINSHOP:
        this.togglerManager.setTogglerWithAction(inv.getInv(), 29, this.actions, i -> this.selectMode(ShopMode.SINGLE_ITEM));
        this.togglerManager.setTogglerWithAction(inv.getInv(), 31, this.actions, i -> this.selectMode(ShopMode.MULTIPLE_ITEM));
      case GLOBALSHOP:
      default:
        this.togglerManager.setTogglerWithAction(inv.getInv(), 33, this.actions, i -> this.selectMode(ShopMode.CATEGORY));
    }
    final Boolean hasReturn = inv.getTransactionValue(TransactionKey.HAS_RETURN);
    if (hasReturn == null || hasReturn)
      this.togglerManager.setTogglerWithAction(inv.getInv(), 0, this.actions, new PreviousInterface());
  }

  @Override
  public void load() {
    super.load();
    this.updateName();
    this.updateAttachedBlock();
  }

  private void selectMode(ShopMode mode) {
    ShopInfo shop =  this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    shop.setMode(mode);
    System.out.println("Mode selected : " + mode);
    // createShop();
  }

  /**
   * Create the shop inside the database and leave the GUI
   * Drop the sign if the shop already exist
   *
   * @param gui InventoryGUI used shop creation
   */
  private void createShop() {
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    try {
      GlobalMarketChest.plugin.shopManager.createShop(shop);

      Utils.editSign(this.inv.getTransactionValue(TransactionKey.SIGN_LOCATION), new String[]{
        shop.getType().getDisplayName()
      });
      PlayerUtils.sendMessageConfig(this.inv.getPlayer(), "InfoMessages.ShopCreated");

    } catch (ShopAlreadyExistException e) {
      PlayerUtils.sendMessage(this.inv.getPlayer(), e.getMessage());
      shop.getSignLocation().ifPresent(loc -> loc.getBlock().breakNaturally());
    } finally {
      Consumer<InventoryGUI> exit = new LeaveShop();
      exit.accept(this.inv);
    }
  }
}
