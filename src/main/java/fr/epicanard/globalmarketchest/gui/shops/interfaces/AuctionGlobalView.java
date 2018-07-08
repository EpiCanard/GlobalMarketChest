package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
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
  class ViewGlobal {
    public StateAuction state = StateAuction.INPROGRESS;
    public AuctionLoreConfig config = AuctionLoreConfig.OWN;
    public Integer pos = 13;

    public void set(StateAuction st, AuctionLoreConfig conf, Integer p) {
      this.state = st;
      this.config = conf;
      this.pos = p;
    }
  }
  ViewGlobal current = new ViewGlobal();

  public AuctionGlobalView(InventoryGUI inv) {
    super(inv);
    this.paginator.setLoadConsumer(pag -> this.loadAuctions());
    this.actions.put(13, i -> this.setPair(StateAuction.INPROGRESS, AuctionLoreConfig.OWN, 13));
    this.actions.put(14, i -> this.setPair(StateAuction.EXPIRED, AuctionLoreConfig.OWN, 14));
    this.actions.put(15, i -> this.setPair(StateAuction.FINISHED, AuctionLoreConfig.SOLD, 15));
    this.actions.put(16, i -> this.setPair(StateAuction.FINISHED, AuctionLoreConfig.BOUGHT, 16));
    this.actions.put(17, i -> this.setPair(StateAuction.ABANDONED, AuctionLoreConfig.OWN, 17));
    this.actions.remove(46);

    this.actions.put(0, new PreviousInterface());
  }

  @Override
  public void load() {
    super.load();
    ItemUtils.setGlow(this.inv.getInv(), 13, true);
  }

  private void setPair(StateAuction state, AuctionLoreConfig config, Integer pos) {
    ItemUtils.setGlow(this.inv.getInv(), this.current.pos, false);
    this.current.set(state, config, pos);
    ItemUtils.setGlow(this.inv.getInv(), this.current.pos, true);
    this.paginator.resetPage();
    this.paginator.reload();
  }

  private void loadAuctions() {
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);

    GlobalMarketChest.plugin.auctionManager.getAuctions(shop.getGroup(), this.current.state, 
      this.current.config == AuctionLoreConfig.BOUGHT ? null : this.inv.getPlayer(),
      this.current.config != AuctionLoreConfig.BOUGHT ? null : this.inv.getPlayer(),
      this.paginator.getLimit(),
      auctions -> {
        this.paginator.setItemStacks(DatabaseUtils.toItemStacks(auctions, (itemstack, auction) -> {
          ItemStackUtils.addItemStackLore(itemstack, auction.getLore(this.current.config));
        }));
      });
  }
}
