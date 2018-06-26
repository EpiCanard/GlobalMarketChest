package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;

public class BuyAuction extends ShopInterface {

  public BuyAuction(InventoryGUI inv) {
    super(inv);
 
    this.actions.put(0, new PreviousInterface());
    this.actions.put(1, this::buyAuction);
  }

  /**
   * Renew the selected auction to current date
   * 
   * @param i
   */
  private void buyAuction(InventoryGUI i) {
    AuctionInfo auction = i.getTransactionValue(TransactionKey.AUCTIONITEM);

    GlobalMarketChest.plugin.auctionManager.buyAuction(auction.getId(), i.getPlayer());
  }
}
