package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;

public class AuctionViewGroup extends ShopInterface {

  public AuctionViewGroup(InventoryGUI inv) {
    super(inv);
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);
    String category = this.inv.getTransactionValue(TransactionKey.CATEGORY);

    this.paginator.setLoadConsumer(pag -> {
      GlobalMarketChest.plugin.auctionManager.getItemByCategory(shop.getGroup(), category, its -> {
        pag.getItemstacks().addAll(pag.getSubList(its));
      });
    });

    this.actions.put(0, new PreviousInterface(() -> {
      this.inv.getTransaction().remove(TransactionKey.CATEGORY);
    }));
  }

  @Override
  public void load() {
    super.load();
  }

  @Override
  public void unload() {
  }
}
