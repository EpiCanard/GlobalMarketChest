package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.AuctionViewBase;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LastAuctionViewList extends AuctionViewBase {
  private Integer lastHours = 24;

  public LastAuctionViewList(final InventoryGUI inv) {
    super(inv);

    this.paginator.setLoadConsumer(pag -> {
      final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);

      GlobalMarketChest.plugin.auctionManager.getLastAuctions(shop.getGroup(), this.lastHours, this.paginator.getLimit(),
        auctions -> {
          if (pag.getLimit().getLeft() == 0 || auctions.size() > 0)
            this.auctions = auctions;
          pag.setItemStacks(DatabaseUtils.toItemStacks(auctions, (itemstack, auction) -> {
            ItemStackUtils.addItemStackLore(itemstack, auction.getLore(AuctionLoreConfig.TOSELL));
          }));
        });
    });

    this.paginator.setClickConsumer(this::selectAuction);
  }

  @Override
  public void load() {
    this.lastHours = LastAuctionViewList.getLastHours();
    this.setIcon(LastAuctionViewList.getLastAuctionsIcon(this.lastHours));
    super.load();
  }

  /**
   * Get icon of view LastAuctionViewList
   *
   * @param hours Hours to add to description of icon
   * @return Icon of view
   */
  public static ItemStack getLastAuctionsIcon(final Integer hours) {
    final ItemStack item = Utils.getButton("LastAuctions");
    final ItemMeta meta = item.getItemMeta();

    meta.setDisplayName(String.format(meta.getDisplayName(), hours));
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Get Hours used for last auctions to display
   *
   * @return Number of hours
   */
  public static Integer getLastHours() {
    return GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt("Options.LastAuctionsHours", 24);
  }
}
