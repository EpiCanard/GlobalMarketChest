package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;

public class EditAuction extends ShopInterface {

  public EditAuction(InventoryGUI inv) {
    super(inv);
 
    this.actions.put(0, new PreviousInterface());
    this.actions.put(1, this::renewAuction);
    this.actions.put(2, this::undoAuction);
  }

  /**
   * Renew the selected auction to current date
   * 
   * @param i
   */
  private void renewAuction(InventoryGUI i) {
    AuctionInfo auction = i.getTransactionValue(TransactionKey.AUCTIONITEM);

    GlobalMarketChest.plugin.auctionManager.renewAuction(auction.getId());
  }

  /**
   * Undo the selected auction
   * 
   * @param i
   */
  private void undoAuction(InventoryGUI i) {
    AuctionInfo auction = i.getTransactionValue(TransactionKey.AUCTIONITEM);
    
    GlobalMarketChest.plugin.auctionManager.undoAuction(auction.getId());    
  }
}
