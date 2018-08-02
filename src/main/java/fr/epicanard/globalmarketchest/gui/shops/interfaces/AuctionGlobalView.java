package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.auctions.StateAuction;
import fr.epicanard.globalmarketchest.exceptions.WarnException;
import fr.epicanard.globalmarketchest.gui.InterfacesLoader;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.DefaultFooter;
import fr.epicanard.globalmarketchest.gui.shops.toggler.SingleToggler;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.ItemUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class AuctionGlobalView extends DefaultFooter {
  class ViewGlobal {
    public StateAuction state = StateAuction.INPROGRESS;
    public AuctionLoreConfig config = AuctionLoreConfig.OWN;
    public Integer pos = 13;
    public List<AuctionInfo> auctions;

    public void set(StateAuction st, AuctionLoreConfig conf, Integer p) {
      this.state = st;
      this.config = conf;
      this.pos = p;
    }
  }
  private ViewGlobal current = new ViewGlobal();

  public AuctionGlobalView(InventoryGUI inv) {
    super(inv);
    this.paginator.setLoadConsumer(pag -> this.loadAuctions());
    this.paginator.setClickConsumer(this::editAuction);

    ItemStack[] items = InterfacesLoader.getInstance().getInterface(this.getClass().getSimpleName());

    this.togglers.put(10, new SingleToggler(inv.getInv(), 10, items[10], Utils.getBackground(), true));
    this.togglers.put(11, new SingleToggler(inv.getInv(), 11, items[11], Utils.getBackground(), true));
    this.actions.put(0, new PreviousInterface());
    this.actions.put(10, this::undoEveryAuction);
    this.actions.put(11, this::renewEveryAuction);
    this.actions.put(13, i -> this.setPair(StateAuction.INPROGRESS, AuctionLoreConfig.OWN, 13));
    this.actions.put(14, i -> this.setPair(StateAuction.EXPIRED, AuctionLoreConfig.OWN, 14));
    this.actions.put(15, i -> this.setPair(StateAuction.FINISHED, AuctionLoreConfig.SOLD, 15));
    this.actions.put(16, i -> this.setPair(StateAuction.FINISHED, AuctionLoreConfig.BOUGHT, 16));
    this.actions.put(17, i -> this.setPair(StateAuction.ABANDONED, AuctionLoreConfig.OWNENDED, 17));
    this.actions.remove(46);
  }

  @Override
  public void load() {
    super.load();
    ItemUtils.setGlow(this.inv.getInv(), this.current.pos, true);
    this.loadTogglers();
  }

  private void setPair(StateAuction state, AuctionLoreConfig config, Integer pos) {
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
  private void loadTogglers() {
    if (this.current.state == StateAuction.INPROGRESS || this.current.state == StateAuction.EXPIRED)
      this.togglers.forEach((key, toggler) -> {
        String lore = String.format(LangUtils.get("Divers.WithStatus"), this.current.state.getLang());
        ItemStackUtils.setItemStackLore(toggler.getSetItem(), Utils.toList(lore));
        toggler.set();
      });
    else
      this.togglers.forEach((key, toggler) -> toggler.unset());
  }

  private void editAuction(Integer pos) {
    if (this.current.state != StateAuction.EXPIRED && this.current.state != StateAuction.INPROGRESS)
      return;
    if (pos >= this.current.auctions.size())
      return;
    AuctionInfo auction = this.current.auctions.get(pos);
    if (auction != null) {
      this.inv.getTransaction().put(TransactionKey.AUCTIONINFO, auction);
      this.inv.loadInterface("EditAuction");
    }
  }

  private void loadAuctions() {
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);

    GlobalMarketChest.plugin.auctionManager.getAuctions(shop.getGroup(), this.current.state, 
      this.current.config == AuctionLoreConfig.BOUGHT ? null : this.inv.getPlayer(),
      this.current.config != AuctionLoreConfig.BOUGHT ? null : this.inv.getPlayer(),
      this.paginator.getLimit(),
      auctions -> {
        this.current.auctions = auctions;
        this.paginator.setItemStacks(DatabaseUtils.toItemStacks(auctions, (itemstack, auction) -> {
          ItemStackUtils.addItemStackLore(itemstack, auction.getLore(this.current.config));
        }));
      });
  }

  /**
   * Renew every auction to current date
   * 
   * @param i
   */
  private void renewEveryAuction(InventoryGUI i) {
    if ((this.current.state != StateAuction.EXPIRED && this.current.state != StateAuction.INPROGRESS) || this.current.auctions.size() == 0)
      return;
    this.inv.getWarn().stopWarn();
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);

    if (GlobalMarketChest.plugin.auctionManager.renewEveryAuctionOfPlayer(i.getPlayer(), shop.getGroup(), this.current.state) == true)
      PlayerUtils.sendMessageConfig(i.getPlayer(), "InfoMessages.RenewEveryAuction");
    else
      i.getWarn().warn("CantRenewEveryAuction", 4);
    this.paginator.resetPage();
    this.paginator.reload();
  }

  /**
   * Renew every auction to current date
   * 
   * @param i
   */
  private void undoEveryAuction(InventoryGUI i) {
    if ((this.current.state != StateAuction.EXPIRED && this.current.state != StateAuction.INPROGRESS) || this.current.auctions.size() == 0)
      return;
    this.inv.getWarn().stopWarn();
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);
    AtomicInteger pos = new AtomicInteger(0);

    try {
      Boolean ret = PlayerUtils.hasEnoughPlace(i.getPlayer().getInventory(), DatabaseUtils.toItemStacks(this.current.auctions, (item, auction) -> item.setAmount(auction.getAmount())), pos);
      List<AuctionInfo> auctions = this.current.auctions.subList(0, pos.get());

      if (GlobalMarketChest.plugin.auctionManager.undoGroupOfPlayerAuctions(i.getPlayer(), shop.getGroup(), Utils.mapList(auctions, act -> act.getId()))) {
        for (AuctionInfo auction : auctions)
          i.getPlayer().getInventory().addItem(auction.getRealItemStack());
        PlayerUtils.sendMessageConfig(i.getPlayer(), "InfoMessages.UndoEveryAuction");
      } else
        throw new WarnException("CantUndoEveryAuction");

      if (!ret)
        throw new WarnException("NotEnoughSpace");
      
    } catch (WarnException e) {
      i.getWarn().warn(e.getMessage(), 4);
    }
    this.paginator.resetPage();
    this.paginator.reload();
  }
}
