package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.auctions.StatusAuction;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.AuctionViewBase;
import fr.epicanard.globalmarketchest.gui.shops.toggler.Toggler;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.ConfigUtils;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class LastAuctionViewList extends AuctionViewBase {
  private Integer lastHours = 24;
  private LastAuctionMode mode = LastAuctionMode.LAST_CREATED;

  public LastAuctionViewList(final InventoryGUI inv) {
    super(inv);

    this.actions.put(7, this::toggleLastAuctionMode);
    this.paginator.setLoadConsumer(pag -> {
      final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);

      GlobalMarketChest.plugin.auctionManager.getLastAuctions(shop.getGroup(), this.lastHours, this.mode.status, this.paginator.getLimit(),
          auctions -> {
            if (pag.getLimit().getLeft() == 0 || auctions.size() > 0)
              this.auctions = auctions;
            pag.setItemStacks(DatabaseUtils.toItemStacks(auctions, (itemstack, auction) -> {
              ItemStackUtils.addItemStackLore(itemstack, auction.getLore(this.mode.loreConfig, this.inv.getPlayer()));
            }));
          }
      );
    });

    this.paginator.setClickConsumer(pos -> {
      if (this.mode == LastAuctionMode.LAST_CREATED) {
        this.selectAuction(pos);
      }
    });
  }

  @Override
  public void load() {
    this.lastHours = LastAuctionViewList.getLastHours();

    this.togglerManager.get(7).setSetItem(Utils.getButton("LastBoughtAuctions", "hours", this.lastHours));
    this.togglerManager.get(7).setUnsetItem(Utils.getButton("LastCreatedAuctions", "hours", this.lastHours));
    super.load();
    this.setIcon(this.togglerManager.get(7).getOppositeItem());
  }

  /**
   * Get Hours used for last auctions to display
   *
   * @return Number of hours
   */
  public static Integer getLastHours() {
    return ConfigUtils.getInt("Options.LastAuctionsHours", 24);
  }

  private void toggleLastAuctionMode(final InventoryGUI gui) {
    this.mode = this.mode.toggle(this.togglerManager.get(7), this.inv.getInv(), this::setIcon);
    this.paginator.resetPage();
    this.paginator.reload();
  }


  private enum LastAuctionMode {
    LAST_CREATED(StatusAuction.IN_PROGRESS, AuctionLoreConfig.TOSELL),
    LAST_BOUGHT(StatusAuction.FINISHED, AuctionLoreConfig.BOUGHT_SOON);

    final StatusAuction status;
    final AuctionLoreConfig loreConfig;

    LastAuctionMode(final StatusAuction status, final AuctionLoreConfig loreConfig) {
      this.status = status;
      this.loreConfig = loreConfig;
    }

    LastAuctionMode toggle(final Toggler toggler, final Inventory inv, final Consumer<ItemStack> setIcon) {
      setIcon.accept(toggler.getCurrentItem());
      toggler.toggle(inv);
      return this == LAST_CREATED ? LAST_BOUGHT : LAST_CREATED;
    }
  }
}
