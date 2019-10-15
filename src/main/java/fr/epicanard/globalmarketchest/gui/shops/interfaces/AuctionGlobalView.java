package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.StateAuction;
import fr.epicanard.globalmarketchest.exceptions.WarnException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.ChatInput;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.BaseAuctionGlobalView;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.*;
import org.bukkit.OfflinePlayer;

public class AuctionGlobalView extends BaseAuctionGlobalView {
  public AuctionGlobalView(InventoryGUI inv) {
    super(inv);
    this.paginator.setClickConsumer(this::editAuction);

    this.actions.put(10, this::undoEveryAuction);
    this.actions.put(11, this::renewEveryAuction);

    if (Permissions.ADMIN_SEEAUCTIONS.isSetOn(inv.getPlayer())) {
      this.togglers.get(6).set();
      this.actions.put(6, new ChatInput("InfoMessages.WritePlayerName", this::openPlayerGlobalView));
    }
  }

  @Override
  public void load() {
    this.playerView = this.inv.getPlayer();
    super.load();
  }

  /**
   * Load and unload togglers
   */
  @Override
  protected void loadTogglers() {
    if (this.current.state == StateAuction.INPROGRESS || this.current.state == StateAuction.EXPIRED)
      this.togglers.forEach((key, toggler) -> {
        if (key != 10 && key != 11)
          return;
        final String lore = String.format(LangUtils.get("Divers.WithStatus"), this.current.state.getLang());
        ItemStackUtils.setItemStackLore(toggler.getSetItem(), Utils.toList(lore));
        toggler.set();
      });
    else
      this.togglers.forEach((key, toggler) -> {
        if (key == 10 || key == 11)
          toggler.unset();
      });
  }

  private void editAuction(Integer pos) {
    if (this.current.state != StateAuction.EXPIRED && this.current.state != StateAuction.INPROGRESS)
      return;
    if (pos >= this.current.auctions.size())
      return;
    final AuctionInfo auction = this.current.auctions.get(pos);
    if (auction != null) {
      this.inv.getTransaction().put(TransactionKey.AUCTION_INFO, auction);
      this.inv.loadInterface("EditAuction");
    }
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
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    final Integer maxAuctionNumber = this.inv.getPlayerRankProperties().getMaxAuctionByPlayer();
    final Integer playerAuctions = this.inv.getTransactionValue(TransactionKey.PLAYER_AUCTIONS);
    List<Integer> auctions = Utils.mapList(this.current.auctions, auction -> auction.getId());
    if (this.current.state == StateAuction.EXPIRED)
      auctions = new ArrayList<>(auctions.subList(0, Utils.getIndex(maxAuctionNumber - playerAuctions, auctions.size(), true)));

    if (auctions.size() > 0 &&
      GlobalMarketChest.plugin.auctionManager.renewGroupOfPlayerAuctions(i.getPlayer(), shop.getGroup(), this.current.state, auctions))
      PlayerUtils.sendMessageConfig(i.getPlayer(), "InfoMessages.RenewEveryAuction");
    else
      i.getWarn().warn("CantRenewEveryAuction", 4);
    this.paginator.resetPage();
    this.paginator.reload();
    this.updateAuctionNumber();
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
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    final AtomicInteger pos = new AtomicInteger(0);

    try {
      final Boolean ret = PlayerUtils.hasEnoughPlace(i.getPlayer().getInventory(), DatabaseUtils.toItemStacks(this.current.auctions, (item, auction) -> item.setAmount(auction.getAmount())), pos);
      final List<AuctionInfo> auctions = this.current.auctions.subList(0, pos.get());

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
    this.updateAuctionNumber();
  }

  private void openPlayerGlobalView(String playerName) {
    final OfflinePlayer offlinePlayer = GlobalMarketChest.plugin.getServer().getOfflinePlayer(playerName);
    this.inv.getTransaction().put(TransactionKey.PLAYER, offlinePlayer);
    this.inv.loadInterface("AdminAuctionGlobalView");
  }

  @Override
  public void destroy() {
    this.inv.getTransaction().remove(TransactionKey.PLAYER);
  }
}
