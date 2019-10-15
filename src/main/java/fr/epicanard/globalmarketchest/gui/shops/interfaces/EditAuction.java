package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.auctions.StateAuction;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.UndoAuction;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.actions.ReturnBack;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

public class EditAuction extends UndoAuction {

  public EditAuction(InventoryGUI inv) {
    super(inv);

    this.isTemp = true;
    this.actions.put(0, new PreviousInterface());
    this.actions.put(33, this::renewAuction);
    this.actions.put(29, this::undoAuction);
  }

  @Override
  public void load() {
    super.load();
    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTION_INFO);
    this.setIcon(ItemStackUtils.addItemStackLore(DatabaseUtils.deserialize(auction.getItemMeta()), auction.getLore(AuctionLoreConfig.OWN)));
  }

  /**
   * Renew the selected auction to current date
   *
   * @param i
   */
  private void renewAuction(InventoryGUI i) {
    final AuctionInfo auction = i.getTransactionValue(TransactionKey.AUCTION_INFO);
    final Integer maxAuctionNumber = this.inv.getPlayerRankProperties().getMaxAuctionByPlayer();
    final Integer playerAuctions = this.inv.getTransactionValue(TransactionKey.PLAYER_AUCTIONS);

    if ((auction.getState() == StateAuction.INPROGRESS || playerAuctions + 1 <= maxAuctionNumber)
        && GlobalMarketChest.plugin.auctionManager.renewAuction(auction.getId())) {
      PlayerUtils.sendMessageConfig(i.getPlayer(), "InfoMessages.RenewAuction");
      ReturnBack.execute(null, this.inv);
    } else {
      i.getWarn().warn("CantRenewAuction", 49);
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    this.inv.getTransaction().remove(TransactionKey.AUCTION_INFO);
  }
}
