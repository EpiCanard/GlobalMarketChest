package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.function.Consumer;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.CategoryHandler;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.gui.shops.DefaultFooter;

public class CategoryView extends DefaultFooter {

  public CategoryView(InventoryGUI inv) {
    super(inv);
  }

  @Override
  public void load() {
    super.load();
    Consumer<InventoryGUI> callable = new NextInterface("AuctionViewGroup");

    CategoryHandler h = GlobalMarketChest.plugin.getCatHandler();
    String[] categories = h.getCategories().toArray(new String[0]);

    for (String category : categories) {
      this.setCategory(category, callable);
    }
    if (GlobalMarketChest.plugin.getConfigLoader().getConfig().getBoolean("Options.UncategorizedItems"))
      this.setCategory("!", callable);
  }

  private void setCategory(final String category, final Consumer<InventoryGUI> callable) {
    CategoryHandler h = GlobalMarketChest.plugin.getCatHandler();

    this.actions.put(h.getPosition(category), in -> {
      this.inv.getTransaction().put(TransactionKey.CATEGORY, category);
      callable.accept(in);
    });

    this.inv.getInv().setItem(h.getPosition(category), h.getDisplayItem(category));
  }
}
