package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import org.apache.commons.lang3.tuple.MutablePair;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.StateAuction;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.DefaultFooter;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.ItemUtils;

public class AuctionGlobalView extends DefaultFooter {
  MutablePair<StateAuction, Boolean> view = new MutablePair<StateAuction,Boolean>(StateAuction.INPROGRESS, true);
  Integer old = 13;

  public AuctionGlobalView(InventoryGUI inv) {
    super(inv);
    this.paginator.setLoadConsumer(pag -> this.loadAuctions());
    this.actions.put(13, i -> this.setPair(StateAuction.INPROGRESS, true, 13));
    this.actions.put(14, i -> this.setPair(StateAuction.EXPIRED, true, 14));
    this.actions.put(15, i -> this.setPair(StateAuction.FINISHED, true, 15));
    this.actions.put(16, i -> this.setPair(StateAuction.FINISHED, false, 16));
    this.actions.put(17, i -> this.setPair(StateAuction.ABANDONED, true, 17));
    this.actions.remove(46);

    this.actions.put(0, new PreviousInterface());
  }

  @Override
  public void load() {
    super.load();
    ItemUtils.setGlow(this.inv.getInv(), 13, true);
  }

  private void setPair(StateAuction state, Boolean playerIsStarter, Integer pos) {
    this.view.setLeft(state);
    this.view.setRight(playerIsStarter);
    this.paginator.resetPage();
    this.paginator.reload();
    ItemUtils.setGlow(this.inv.getInv(), this.old, false);
    this.old = pos;
    ItemUtils.setGlow(this.inv.getInv(), this.old, true);
  }

  private void loadAuctions() {
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);

    GlobalMarketChest.plugin.auctionManager.getAuctions(shop.getGroup(), this.view.left, 
      !this.view.right ? null : this.inv.getPlayer(),
      this.view.right ? null : this.inv.getPlayer(),
      this.paginator.getLimit(),
      auctions -> {
        this.paginator.setItemStacks(DatabaseUtils.toItemStacks(auctions, (itemstack, auction) -> {
          ItemStackUtils.addItemStackLore(itemstack, auction.getLore(false, true));
        }));
      });
  }
}
