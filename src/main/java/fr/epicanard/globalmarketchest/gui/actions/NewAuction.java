package fr.epicanard.globalmarketchest.gui.actions;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionType;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.shops.ShopInfo;

import java.util.function.Consumer;

/**
 * Consumer to create Auction
 */
public class NewAuction implements Consumer<InventoryGUI> {

  @Override
  public void accept(InventoryGUI inv) {
    ShopInfo shop = inv.getTransactionValue(TransactionKey.SHOP_INFO);
    Integer maxAuctions = inv.getPlayerRankProperties().getMaxAuctionByPlayer();

    GlobalMarketChest.plugin.auctionManager.getAuctionNumber(shop.getGroup(), inv.getPlayer(), auctionNumber -> {
      if (auctionNumber >= maxAuctions) {
        inv.getWarn().warn("MaxAuctionByPlayer", 52);
      } else {
        AuctionInfo info = new AuctionInfo(AuctionType.SELL, inv.getPlayer(), shop.getGroup());
        inv.getTransaction().put(TransactionKey.AUCTION_INFO, info);
        inv.loadInterface(InterfaceType.CREATE_AUCTION_ITEM);
      }
    });
  }
}
