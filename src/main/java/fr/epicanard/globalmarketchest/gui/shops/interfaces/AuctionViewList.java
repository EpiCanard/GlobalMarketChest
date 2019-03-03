package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.DefaultFooter;
import fr.epicanard.globalmarketchest.managers.GroupLevels;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class AuctionViewList extends DefaultFooter {
  private List<Pair<ItemStack, AuctionInfo>> auctions = new ArrayList<>();
  private GroupLevels level;
  private String category;
  private ItemStack item;

  public AuctionViewList(InventoryGUI inv) {
    super(inv);

    this.item = this.inv.getTransactionValue(TransactionKey.AUCTIONITEM);
    this.category = this.inv.getTransactionValue(TransactionKey.CATEGORY);
    this.level = Optional.ofNullable((GroupLevels)this.inv.getTransactionValue(TransactionKey.GROUPLEVEL))
      .orElse(GroupLevels.LEVEL1);

    this.paginator.setLoadConsumer(pag -> {
      ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);

      GlobalMarketChest.plugin.auctionManager.getAuctions(this.level, shop.getGroup(), this.category, item, this.paginator.getLimit(),
          auctions -> {
            if (pag.getLimit().getLeft() == 0 || auctions.size() > 0)
              this.auctions = auctions;
            pag.setItemStacks(Utils.mapList(auctions, auction -> {
              ItemStack it = auction.getLeft();
              int max = inv.getInv().getMaxStackSize();
              if (this.level.getNextLevel(this.category) == null) {
                it.setAmount(auction.getRight().getAmount() > max ? max : auction.getRight().getAmount());
                ItemStackUtils.addItemStackLore(it, auction.getRight().getLore(AuctionLoreConfig.TOSELL));
              }
              return it;
            }));
          });
    });

    this.paginator.setClickConsumer(this::selectAuction);

    this.actions.put(0, new PreviousInterface());
  }

  @Override
  public void load() {
    this.setIcon(this.item);
    super.load();
  }

  private void selectAuction(Integer pos) {
    if (pos >= this.auctions.size() || pos < 0)
      return;
    AuctionInfo auction = this.auctions.get(pos).getRight();
    Boolean isOwner = auction.getPlayerStarter().equals(PlayerUtils.getUUIDToString(this.inv.getPlayer()));

    if (this.level.getNextLevel(category) == null) {
      if (!isOwner && !Permissions.GS_BUYAUCTION.isSetOnWithMessage(this.inv.getPlayer()))
        return;

      this.inv.getTransaction().put(TransactionKey.AUCTIONINFO, auction);
      if (isOwner)
        this.inv.loadInterface("EditAuction");
      else
        this.inv.loadInterface("BuyAuction");
    } else {
      this.inv.getTransaction().put(TransactionKey.GROUPLEVEL, this.level.getNextLevel(category));
      this.inv.getTransaction().put(TransactionKey.AUCTIONITEM, DatabaseUtils.deserialize(auction.getItemMeta()));
      this.inv.loadInterface("AuctionViewList");
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    this.inv.getTransaction().remove(TransactionKey.AUCTIONITEM);
    this.inv.getTransaction().remove(TransactionKey.GROUPLEVEL);
  }
}
