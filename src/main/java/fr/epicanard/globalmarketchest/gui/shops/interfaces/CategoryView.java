package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.CategoryHandler;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.DefaultFooter;
import fr.epicanard.globalmarketchest.managers.GroupLevels;

public class CategoryView extends DefaultFooter {

  public CategoryView(InventoryGUI inv) {
    super(inv);
  }

  @Override
  public void load() {
    super.load();
    Consumer<InventoryGUI> callable = new NextInterface("AuctionViewList");

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
      this.inv.getTransaction().put(TransactionKey.AUCTIONITEM, GlobalMarketChest.plugin.getCatHandler().getDisplayItem(category));
      this.inv.getTransaction().put(TransactionKey.GROUPLEVEL, GroupLevels.LEVEL1);
      callable.accept(in);
    });

    this.inv.getInv().setItem(h.getPosition(category), h.getDisplayItem(category));
  }

  /**
   * Called when a mouse drop event is done inside inventory
   *
   * @param event
   */
  @Override
  public void onDrop(InventoryClickEvent event, InventoryGUI inv) {
    if (!GlobalMarketChest.plugin.getConfigLoader().getConfig().getBoolean("Options.EnableSimilarAuctions", true)) {
      return;
    }
    ItemStack item = null;

    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
      item = event.getCurrentItem();
    } else {
      item = event.getCursor();
    }

    String category = GlobalMarketChest.plugin.getCatHandler().getCategory(item);
    Integer numberLevels = GlobalMarketChest.plugin.getCatHandler().getGroupLevels(category);
    GroupLevels lastLevel = this.getLastLevel(GroupLevels.LEVEL1, numberLevels);

    this.inv.getTransaction().put(TransactionKey.GROUPLEVEL, lastLevel);
    this.inv.getTransaction().put(TransactionKey.CATEGORY, category);
    this.inv.getTransaction().put(TransactionKey.AUCTIONITEM, item);

    new NextInterface("AuctionViewList").accept(inv);
  }

  /**
   * Get the last level possible with numberLevels specified
   * 
   * @param level GroupLevels to compare
   * @param numberLevels Number of levels for category
   * @return Return the last group level of category
   */
  private GroupLevels getLastLevel(GroupLevels level, Integer numberLevels) {
    GroupLevels nextLevel = level.getNextLevel(numberLevels);
    return (nextLevel == null) ? level : this.getLastLevel(nextLevel, numberLevels);
  }
}
