package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.StatusAuction;
import fr.epicanard.globalmarketchest.exceptions.WarnException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.ChatInput;
import fr.epicanard.globalmarketchest.gui.actions.InterfaceType;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.BaseAuctionGlobalView;
import fr.epicanard.globalmarketchest.gui.shops.toggler.Toggler;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.epicanard.globalmarketchest.utils.LangUtils.format;

public class AuctionGlobalView extends BaseAuctionGlobalView {
  public AuctionGlobalView(InventoryGUI inv) {
    super(inv);
    this.paginator.setClickConsumer(this::editAuction);

    this.actions.put(10, this::undoEveryAuction);
    this.actions.put(11, this::renewEveryAuction);

    if (Permissions.ADMIN_SEEAUCTIONS.isSetOn(inv.getPlayer()))
      this.togglerManager.setTogglerWithAction(inv.getInv(), 6, this.actions, new ChatInput("InfoMessages.WritePlayerName", this::openPlayerGlobalView));
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
    if (this.current.state == StatusAuction.IN_PROGRESS || this.current.state == StatusAuction.EXPIRED) {
      final String lore = format("Divers.WithStatus", "status", this.current.state.getLang());
      for (Integer pos: new Integer[]{10, 11}) {
        Toggler toggler = this.togglerManager.get(pos);
        ItemStackUtils.setItemStackLore(toggler.getSetItem(), Utils.toList(lore));
        toggler.set(this.inv.getInv());
      }
    } else
      this.togglerManager.unsetTogglers(this.inv.getInv(), 10, 11);
  }

  private void editAuction(Integer pos) {
    if (this.current.state != StatusAuction.EXPIRED && this.current.state != StatusAuction.IN_PROGRESS)
      return;
    if (pos >= this.current.auctions.size())
      return;
    final AuctionInfo auction = this.current.auctions.get(pos);
    if (auction != null) {
      this.inv.getTransaction().put(TransactionKey.AUCTION_INFO, auction);
      this.inv.loadInterface(InterfaceType.EDIT_AUCTION);
    }
  }

  /**
   * Renew every auction to current date
   *
   * @param i
   */
  private void renewEveryAuction(InventoryGUI i) {
    if ((this.current.state != StatusAuction.EXPIRED && this.current.state != StatusAuction.IN_PROGRESS) || this.current.auctions.size() == 0)
      return;
    this.inv.getWarn().stopWarn();
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    final Integer maxAuctionNumber = this.inv.getPlayerRankProperties().getMaxAuctionByPlayer();
    final Integer playerAuctions = this.inv.getTransactionValue(TransactionKey.PLAYER_AUCTIONS);
    List<Integer> auctions = Utils.mapList(this.current.auctions, AuctionInfo::getId);
    if (this.current.state == StatusAuction.EXPIRED)
      auctions = new ArrayList<>(auctions.subList(0, Utils.getIndex(maxAuctionNumber - playerAuctions, auctions.size(), true)));

    final Integer expirationDays = i.getPlayerRankProperties().getNumberDaysExpiration();
    if (auctions.size() > 0 && GlobalMarketChest.plugin.auctionManager
        .renewGroupOfPlayerAuctions(i.getPlayer(), shop.getGroup(), this.current.state, auctions, expirationDays))
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
    if ((this.current.state != StatusAuction.EXPIRED && this.current.state != StatusAuction.IN_PROGRESS) || this.current.auctions.size() == 0)
      return;
    this.inv.getWarn().stopWarn();
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    final AtomicInteger pos = new AtomicInteger(0);

    try {
      final Boolean ret = PlayerUtils.hasEnoughPlace(
          i.getPlayer().getInventory(),
          DatabaseUtils.toItemStacks(this.current.auctions, (item, auction) -> item.setAmount(auction.getAmount())),
          pos
      );
      final List<AuctionInfo> auctions = this.current.auctions.subList(0, pos.get());

      if (auctions.size() > 0) {
        List<Integer> ids = GlobalMarketChest.plugin.auctionManager
            .undoGroupOfPlayerAuctions(i.getPlayer(), shop.getGroup(), Utils.mapList(auctions, AuctionInfo::getId));

        if (ids.size() > 0) {
          for (AuctionInfo auction : auctions)
            if (ids.contains(auction.getId()))
              i.getPlayer().getInventory().addItem(auction.getRealItemStack());
          PlayerUtils.sendMessageConfig(i.getPlayer(), "InfoMessages.UndoEveryAuction");
        } else
          throw new WarnException("CantUndoEveryAuction");
      }

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
    this.inv.loadInterface(InterfaceType.ADMIN_AUCTION_GLOBAL_VIEW);
  }

  @Override
  public void destroy() {
    this.inv.getTransaction().remove(TransactionKey.PLAYER);
  }
}
