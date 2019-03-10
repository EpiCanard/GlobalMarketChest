package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import org.bukkit.OfflinePlayer;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.auctions.StateAuction;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.AuctionViewBase;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

public class AuctionViewByPlayer extends AuctionViewBase {
  public AuctionViewByPlayer(InventoryGUI inv) {
    super(inv);

    this.paginator.setLoadConsumer(pag -> {
      final OfflinePlayer player = this.inv.getTransactionValue(TransactionKey.PLAYER);
      final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);

      GlobalMarketChest.plugin.auctionManager.getAuctions(shop.getGroup(), StateAuction.INPROGRESS, player, null, this.paginator.getLimit(),
          auctions -> {
            if (pag.getLimit().getLeft() == 0 || auctions.size() > 0)
              this.auctions = auctions;
            pag.setItemStacks(DatabaseUtils.toItemStacks(auctions, (itemstack, auction) -> {
              ItemStackUtils.addItemStackLore(itemstack, auction.getLore(AuctionLoreConfig.TOSELL));
            }));
          });
    });
  }

  @Override
  public void load() {
    this.setIcon(PlayerUtils.getPlayerHead(this.inv.getTransactionValue(TransactionKey.PLAYER)));
    super.load();
  }

  @Override
  public void destroy() {
    super.destroy();
    this.inv.getTransaction().remove(TransactionKey.PLAYER);
    this.inv.getTransaction().remove(TransactionKey.AUCTIONINFO);
  }
}
