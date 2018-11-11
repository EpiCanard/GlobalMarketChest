package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.function.Consumer;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.CategoryHandler;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.gui.shops.DefaultFooter;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class CategoryView extends DefaultFooter {

  public CategoryView(InventoryGUI inv) {
    super(inv);
  }

  @Override
  public void load() {
    super.load();
    Consumer<InventoryGUI> callable = new NextInterface("AuctionViewGroup");

    CategoryHandler h = GlobalMarketChest.plugin.getCatHandler();
    String[] cat = h.getCategories().toArray(new String[0]);
    for (int i = 0; i < cat.length; i++) {
      int pos = Utils.toPos(i % 5 + 2, (i / 5) * 2 + 2);
      final int j = i;
      this.setActionCategory(pos, cat[j], callable);
      this.inv.getInv().setItem(pos, h.getDisplayItem(cat[i]));
    }
    if (GlobalMarketChest.plugin.getConfigLoader().getConfig().getBoolean("Options.UncategorizedItems"))
      this.setActionCategory(31, "!", callable);
  }

  private void setActionCategory(final int pos, final String category, final Consumer<InventoryGUI> callable) {
    this.actions.put(pos, in -> {
      this.inv.getTransaction().put(TransactionKey.CATEGORY, category);
      callable.accept(in);
    });
  }
}
