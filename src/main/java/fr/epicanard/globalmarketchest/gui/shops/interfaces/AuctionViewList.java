package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionGroup;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.DefaultFooter;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

public class AuctionViewList extends DefaultFooter {
  private List<AuctionInfo> auctions = new ArrayList<>();

  public AuctionViewList(InventoryGUI inv) {
    super(inv);

    this.paginator.setLoadConsumer(pag -> {
      ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);
      ItemStack item = this.inv.getTransactionValue(TransactionKey.AUCTIONITEM);
      this.setIcon(item);

      GlobalMarketChest.plugin.auctionManager.getAuctionsByItem(shop.getGroup(), item, this.paginator.getLimit(),
          auctions -> {
            groupAuctions(auctions);
            this.auctions = auctions;
            pag.setItemStacks(DatabaseUtils.toItemStacks(auctions, (itemstack, auction) -> {
              ItemStackUtils.addItemStackLore(itemstack, auction.getLore(AuctionLoreConfig.TOSELL));
            }));
          });
    });

    this.paginator.setClickConsumer(this::selectAuction);

    this.actions.put(0, new PreviousInterface());
  }

  /**
   * Group similar auctions from the same player and same price together
   *
   * @param auctions The list of auctions to group together
   * @return Return a list of auctions group
   */
  private List<AuctionGroup> groupAuctions(List<AuctionInfo> auctions) {
    List<AuctionGroup> groups = new ArrayList<>();

    auctions.forEach(auction -> {
      if (groups.size() > 0) {
        AuctionGroup last = groups.get(groups.size() - 1);
        AuctionInfo lastAuction = last.getAuction();
        if (lastAuction.getItemMeta() == auction.getItemMeta() &&
          lastAuction.getAmount() == auction.getAmount() &&
          lastAuction.getPrice().compareTo(auction.getPrice()) == 0 &&
          lastAuction.getPlayerStarter() == auction.getPlayerStarter()) {
          last.addAuction(auction);
        }
      }
      groups.add(new AuctionGroup(auction));

    });

    return groups;
  }

  private void selectAuction(Integer pos) {
    if (pos >= this.auctions.size())
      return;
    AuctionInfo auction = this.auctions.get(pos);
    Boolean isOwner = auction.getPlayerStarter().equals(PlayerUtils.getUUIDToString(this.inv.getPlayer()));

    if (!isOwner && !Permissions.GS_BUYAUCTION.isSetOnWithMessage(this.inv.getPlayer()))
      return;

    this.inv.getTransaction().put(TransactionKey.AUCTIONINFO, auction);
    if (isOwner)
      this.inv.loadInterface("EditAuction");
    else
      this.inv.loadInterface("BuyAuction");
  }

  @Override
  public void destroy() {
    super.destroy();
    this.inv.getTransaction().remove(TransactionKey.AUCTIONITEM);
  }
}
