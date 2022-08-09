package fr.epicanard.globalmarketchest.gui.shops.baseinterfaces;

import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

import static fr.epicanard.globalmarketchest.permissions.Permissions.*;

public class AuctionViewBase extends DefaultFooter {
  protected List<AuctionInfo> auctions = new ArrayList<>();

  public AuctionViewBase(InventoryGUI inv) {
    super(inv);

    this.paginator.setClickConsumer(this::selectAuction);

    this.actions.put(0, new PreviousInterface());
  }

  @Override
  public void load() {
    super.load();
  }

  /**
   * This method is called when player click on item inside paginator
   * If it is the owner auction it open edit interface
   * If it is not it open buy interface
   *
   * @param pos Position clicked inside paginator
   */
  protected void selectAuction(Integer pos) {
    if (pos >= this.auctions.size() || pos < 0)
      return;
    AuctionInfo auction = this.auctions.get(pos);
    Boolean isOwner = auction.getPlayerStarter().equals(PlayerUtils.getUUIDToString(this.inv.getPlayer()));

    if (!isOwner && !GS_BUYAUCTION.isSetOn(this.inv.getPlayer()) && !GS_SHOP_BUYAUCTION.isSetOnWithShop(this.inv.getPlayer(), this.shopInfo.getGroup())) {
      return;
    }

    this.inv.getTransaction().put(TransactionKey.AUCTION_INFO, auction);
    if (isOwner)
      this.inv.loadInterface("EditAuction");
    else
      this.inv.loadInterface("BuyAuction");
  }

  @Override
  public void destroy() {
    super.destroy();
  }
}
