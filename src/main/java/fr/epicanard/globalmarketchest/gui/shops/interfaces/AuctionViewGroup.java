package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.NewAuction;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;

public class AuctionViewGroup extends ShopInterface {

  public AuctionViewGroup(InventoryGUI inv) {
    super(inv);
    String category = this.inv.getTransactionValue(TransactionKey.CATEGORY);
    this.setIcon(GlobalMarketChest.plugin.getCatHandler().getDisplayItem(category));

    this.paginator.setLoadConsumer(pag -> {
      ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);
      GlobalMarketChest.plugin.auctionManager.getItemByCategory(shop.getGroup(), category, pag.getLimit(), pag::setItemStacks);
    });
    this.paginator.setClickConsumer(pos -> {
      ItemStack item = this.paginator.getItemStack(pos);
      if (item == null)
        return;
      this.inv.getTransaction().put(TransactionKey.AUCTIONITEM, item);
      this.inv.loadInterface("AuctionViewList");
    });

    this.actions.put(0, new PreviousInterface());
    this.actions.put(53, new NewAuction());
  }

  @Override
  public void destroy() {
    super.destroy();
    this.inv.getTransaction().remove(TransactionKey.CATEGORY);
  }
}
