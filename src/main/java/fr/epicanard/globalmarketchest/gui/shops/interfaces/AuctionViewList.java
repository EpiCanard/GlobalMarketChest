package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.AuctionViewBase;
import fr.epicanard.globalmarketchest.managers.GroupLevels;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.*;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class AuctionViewList extends AuctionViewBase {
  private GroupLevels level;
  private String category;
  private ItemStack item;
  private AuctionInfo auctionRef;
  private ShopInfo shopInfo;

  private final Integer days = ConfigUtils.getInt("Options.AdvicePrice.Days", 30);
  private final String analyze = ConfigUtils.getString("Options.AdvicePrice.Analyze", "all");


  public AuctionViewList(InventoryGUI inv) {
    super(inv);

    this.item = this.inv.getTransactionValue(TransactionKey.AUCTION_ITEM);
    this.category = this.inv.getTransactionValue(TransactionKey.CATEGORY);
    this.auctionRef = this.inv.getTransactionValue(TransactionKey.AUCTION_INFO);
    this.shopInfo = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    this.level = Optional.ofNullable((GroupLevels) this.inv.getTransactionValue(TransactionKey.GROUP_LEVEL))
        .orElse(GroupLevels.LEVEL1);

    this.paginator.setLoadConsumer(pag -> {
      GlobalMarketChest.plugin.auctionManager.getAuctions(this.level, this.shopInfo.getGroup(), this.category, this.auctionRef, this.paginator.getLimit(),
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
    super.load();
    if (this.level.getNextLevel(category) == null && this.auctionRef != null) {
      this.buildAdvicePrice();
    } else {
      this.setIcon(this.item);
    }
  }

  /**
   * Build the icon with advice price
   */
  private void buildAdvicePrice() {
    GlobalMarketChest.plugin.auctionManager.getAveragePriceItem(auctionRef.getItemMeta(), shopInfo.getGroup(), days, analyze, price -> {
      String adviceMessage;
      if (price != null) {
        adviceMessage = LangUtils.format("Divers.AdvicePriceIcon", "advicePrice", EconomyUtils.format(price));
      } else {
        adviceMessage = LangUtils.get("Divers.NoAdvicePrice");
      }

      final ItemStack iconItem = ItemStackUtils.addItemStackLore(this.item.clone(), Utils.toList("&6--------------", adviceMessage));
      this.setIcon(iconItem);
    });

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
      this.inv.getTransaction().put(TransactionKey.AUCTION_INFO, auction);
      this.inv.getTransaction().put(TransactionKey.GROUP_LEVEL, this.level.getNextLevel(category));
      this.inv.getTransaction().put(TransactionKey.AUCTION_ITEM, DatabaseUtils.deserialize(auction.getItemMeta()));
      this.inv.loadInterface("AuctionViewList");
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    this.inv.getTransaction().remove(TransactionKey.AUCTION_INFO);
    this.inv.getTransaction().remove(TransactionKey.AUCTION_ITEM);
    this.inv.getTransaction().remove(TransactionKey.GROUP_LEVEL);
  }
}
