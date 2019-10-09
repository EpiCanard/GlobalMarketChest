package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.StateAuction;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.BaseAuctionGlobalView;
import fr.epicanard.globalmarketchest.utils.*;
import org.bukkit.OfflinePlayer;

public class AdminAuctionGlobalView extends BaseAuctionGlobalView {

  public AdminAuctionGlobalView(InventoryGUI inv) {
    super(inv);
    this.paginator.setClickConsumer(this::buyAuction);
  }

  @Override
  public void load() {
    final OfflinePlayer auctionsPlayer = this.inv.getTransactionValue(TransactionKey.PLAYER);
    this.playerView = (auctionsPlayer != null) ? auctionsPlayer : this.inv.getPlayer();
    super.load();

    if (auctionsPlayer != null) {
      this.setIcon(PlayerUtils.getPlayerHead(auctionsPlayer));
    }
  }

  @Override
  protected void loadTogglers() {}

  private void buyAuction(Integer pos) {
    if (this.current.state != StateAuction.INPROGRESS)
      return;
    if (pos >= this.current.auctions.size())
      return;
    final AuctionInfo auction = this.current.auctions.get(pos);
    if (auction != null) {
      this.inv.getTransaction().put(TransactionKey.AUCTIONINFO, auction);
      this.inv.loadInterface("BuyAuction");
    }
  }

  @Override
  public void destroy() {
    this.inv.getWarn().stopWarn();
    this.inv.getTransaction().remove(TransactionKey.PLAYER);
  }
}
