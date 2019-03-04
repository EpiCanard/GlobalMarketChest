package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_13_R2.CraftOfflinePlayer;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.auctions.StateAuction;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.DefaultFooter;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

public class AuctionViewByPlayer extends DefaultFooter {
  private List<AuctionInfo> auctions = new ArrayList<>();

  public AuctionViewByPlayer(InventoryGUI inv) {
    super(inv);

    this.paginator.setLoadConsumer(pag -> {
      final OfflinePlayer player = this.inv.getTransactionValue(TransactionKey.PLAYER);
      ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);

      GlobalMarketChest.plugin.auctionManager.getAuctions(shop.getGroup(), StateAuction.INPROGRESS, player, null, this.paginator.getLimit(),
          auctions -> {
            if (pag.getLimit().getLeft() == 0 || auctions.size() > 0)
              this.auctions = auctions;
            pag.setItemStacks(DatabaseUtils.toItemStacks(auctions, (itemstack, auction) -> {
              ItemStackUtils.addItemStackLore(itemstack, auction.getLore(AuctionLoreConfig.TOSELL));
            }));
          });
    });

    this.paginator.setClickConsumer(this::selectAuction);

    this.actions.put(0, new PreviousInterface());
  }

  @Override
  public void load() {
    this.setIcon(PlayerUtils.getPlayerHead(this.inv.getTransactionValue(TransactionKey.PLAYER)));
    super.load();
  }

  private void selectAuction(Integer pos) {
    if (pos >= this.auctions.size() || pos < 0)
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
    this.inv.getTransaction().remove(TransactionKey.PLAYER);
    this.inv.getTransaction().remove(TransactionKey.AUCTIONINFO);
  }
}
