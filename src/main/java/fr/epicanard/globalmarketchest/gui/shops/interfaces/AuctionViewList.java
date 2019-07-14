package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.Optional;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.AuctionViewBase;
import fr.epicanard.globalmarketchest.managers.GroupLevels;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class AuctionViewList extends AuctionViewBase {
  private GroupLevels level;
  private String category;
  private ItemStack item;
  private AuctionInfo auctionRef;

  public AuctionViewList(InventoryGUI inv) {
    super(inv);

    this.item = this.inv.getTransactionValue(TransactionKey.AUCTIONITEM);
    this.category = this.inv.getTransactionValue(TransactionKey.CATEGORY);
    this.auctionRef = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);
    this.level = Optional.ofNullable((GroupLevels) this.inv.getTransactionValue(TransactionKey.GROUPLEVEL))
      .orElse(GroupLevels.LEVEL1);

    this.paginator.setLoadConsumer(pag -> {
      final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);

      GlobalMarketChest.plugin.auctionManager.getAuctions(this.level, shop.getGroup(), this.category, this.auctionRef, this.paginator.getLimit(),
          auctions -> {
            if (pag.getLimit().getLeft() == 0 || auctions.size() > 0)
              this.auctions = Utils.mapList(auctions, auction -> auction.getRight());
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
  }

  @Override
  public void load() {
    this.setIcon(this.item);
    super.load();
  }

  /**
   * This method is called when player click on item inside paginator
   * If it is the last level it called the super selectAuction method
   * Else it load a new interface to the next level
   *
   * @param pos Position clicked inside paginator
   */
  @Override
  protected void selectAuction(Integer pos) {
    if (pos >= this.auctions.size() || pos < 0)
      return;

    final AuctionInfo auction = this.auctions.get(pos);

    if (this.level.getNextLevel(category) == null) {
      super.selectAuction(pos);
    } else {
      this.inv.getTransaction().put(TransactionKey.AUCTIONINFO, auction);
      this.inv.getTransaction().put(TransactionKey.GROUPLEVEL, this.level.getNextLevel(category));
      this.inv.getTransaction().put(TransactionKey.AUCTIONITEM, DatabaseUtils.deserialize(auction.getItemMeta()));
      this.inv.loadInterface("AuctionViewList");
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    this.inv.getTransaction().remove(TransactionKey.AUCTIONINFO);
    this.inv.getTransaction().remove(TransactionKey.AUCTIONITEM);
    this.inv.getTransaction().remove(TransactionKey.GROUPLEVEL);
  }
}
