package fr.epicanard.globalmarketchest.gui.shops.baseinterfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.auctions.StatusAuction;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.paginator.Paginator;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.ItemUtils;
import org.bukkit.OfflinePlayer;

import java.util.List;

public abstract class BaseAuctionGlobalView extends DefaultFooter {
  protected class ViewGlobal {
    public StatusAuction state = StatusAuction.IN_PROGRESS;
    public AuctionLoreConfig config = AuctionLoreConfig.OWN;
    public Integer pos = 13;
    public List<AuctionInfo> auctions;

    public void set(StatusAuction st, AuctionLoreConfig conf, Integer p) {
      this.state = st;
      this.config = conf;
      this.pos = p;
    }
  }

  protected ViewGlobal current;
  protected OfflinePlayer playerView;

  public BaseAuctionGlobalView(InventoryGUI inv) {
    super(inv);
    this.current = new ViewGlobal();
    this.paginator.setLoadConsumer(this::loadAuctions);

    this.actions.put(0, new PreviousInterface());
    this.actions.put(13, i -> this.setPair(StatusAuction.IN_PROGRESS, AuctionLoreConfig.OWN, 13));
    this.actions.put(14, i -> this.setPair(StatusAuction.EXPIRED, AuctionLoreConfig.OWN, 14));
    this.actions.put(15, i -> this.setPair(StatusAuction.FINISHED, AuctionLoreConfig.SOLD, 15));
    this.actions.put(16, i -> this.setPair(StatusAuction.FINISHED, AuctionLoreConfig.BOUGHT, 16));
    this.actions.put(17, i -> this.setPair(StatusAuction.ABANDONED, AuctionLoreConfig.OWNENDED, 17));
    this.actions.remove(46);
  }

  @Override
  public void load() {
    super.load();
    ItemUtils.setGlow(this.inv.getInv(), this.current.pos, true);
    this.loadTogglers();
  }

  private void setPair(StatusAuction state, AuctionLoreConfig config, Integer pos) {
    ItemUtils.setGlow(this.inv.getInv(), this.current.pos, false);
    this.current.set(state, config, pos);
    ItemUtils.setGlow(this.inv.getInv(), this.current.pos, true);
    this.inv.getWarn().stopWarn();
    this.loadTogglers();
    this.paginator.resetPage();
    this.paginator.reload();
  }

  /**
   * Load and unload togglers
   */
  protected abstract void loadTogglers();

  private void loadAuctions(Paginator pag) {
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);

    GlobalMarketChest.plugin.auctionManager.getAuctions(shop.getGroup(), this.current.state,
        this.current.config == AuctionLoreConfig.BOUGHT ? null : this.playerView,
        this.current.config != AuctionLoreConfig.BOUGHT ? null : this.playerView,
        pag.getLimit(),
        auctions -> {
          if (pag.getLimit().getLeft() == 0 || auctions.size() > 0)
            this.current.auctions = auctions;
          pag.setItemStacks(DatabaseUtils.toItemStacks(auctions, (itemstack, auction) -> {
            ItemStackUtils.addItemStackLore(itemstack, auction.getLore(this.current.config, this.inv.getPlayer()));
          }));
        });
  }

  @Override
  public void destroy() {
    this.inv.getWarn().stopWarn();
    this.inv.getTransaction().remove(TransactionKey.PLAYER);
  }
}
