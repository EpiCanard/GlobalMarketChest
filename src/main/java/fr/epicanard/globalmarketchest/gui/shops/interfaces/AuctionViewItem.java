package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.Arrays;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.AuctionViewBase;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class AuctionViewItem extends AuctionViewBase {
  public AuctionViewItem(InventoryGUI inv) {
    super(inv);

    this.paginator.setLoadConsumer(pag -> {
      final String search = this.inv.getTransactionValue(TransactionKey.ITEM_SEARCH);
      final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);

      GlobalMarketChest.plugin.auctionManager.getAuctionsByItemName(shop.getGroup(), search, this.paginator.getLimit(),
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
    final String search = this.inv.getTransactionValue(TransactionKey.ITEM_SEARCH);

    final ItemStack  icon = Utils.getButton("SearchItemText");
    ItemStackUtils.addItemStackLore(icon, Arrays.asList(String.format(LangUtils.get("Divers.SearchItemTextIcon"), search)));
    this.setIcon(icon);

    super.load();
  }

  @Override
  public void destroy() {
    super.destroy();
    this.inv.getTransaction().remove(TransactionKey.ITEM_SEARCH);
  }
}
