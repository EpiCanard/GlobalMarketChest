package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.ChatInput;
import fr.epicanard.globalmarketchest.gui.actions.InterfaceType;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.Droppable;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.DefaultFooter;
import fr.epicanard.globalmarketchest.managers.GroupLevels;
import fr.epicanard.globalmarketchest.utils.ConfigUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SearchView extends DefaultFooter implements Droppable {

  public SearchView(InventoryGUI inv) {
    super(inv);

    this.actions.put(0, new PreviousInterface());
    this.actions.put(22, new ChatInput("InfoMessages.WriteItemName", this::searchItem));
    this.actions.put(25, new ChatInput("InfoMessages.WritePlayerName", this::searchPlayer));

  }

  @Override
  public void load() {
    super.load();
    this.setIcon(Utils.getButton("Search"));
  }


  /**
   * Called when a mouse drop event is done inside inventory
   *
   * @param event
   */
  @Override
  public void onDrop(InventoryClickEvent event, InventoryGUI inv) {
    if (!ConfigUtils.getBoolean("Options.EnableSimilarAuctions", true)) {
      return;
    }
    ItemStack item = null;

    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
      item = event.getCurrentItem();
    } else if (event.getSlot() == 19) {
      item = event.getCursor();
      event.getWhoClicked().setItemOnCursor(null);
      event.getWhoClicked().getInventory().addItem(item.clone());
    } else {
      return;
    }

    final String category = GlobalMarketChest.plugin.getCatHandler().getCategory(item);
    final Integer numberLevels = GlobalMarketChest.plugin.getCatHandler().getGroupLevels(category);
    final GroupLevels lastLevel = this.getLastLevel(GroupLevels.LEVEL1, numberLevels);

    this.inv.getTransaction().put(TransactionKey.GROUP_LEVEL, lastLevel);
    this.inv.getTransaction().put(TransactionKey.CATEGORY, category);
    this.inv.getTransaction().put(TransactionKey.AUCTION_ITEM, item);
    this.inv.getTransaction().put(TransactionKey.AUCTION_INFO, new AuctionInfo(item));

    this.inv.loadInterface(InterfaceType.AUCTION_VIEW_LIST);
  }

  /**
   * Get the last level possible with numberLevels specified
   *
   * @param level GroupLevels to compare
   * @param numberLevels Number of levels for category
   * @return Return the last group level of category
   */
  private GroupLevels getLastLevel(GroupLevels level, Integer numberLevels) {
    final GroupLevels nextLevel = level.getNextLevel(numberLevels);
    return (nextLevel == null) ? level : this.getLastLevel(nextLevel, numberLevels);
  }

  private void searchItem(String itemName) {
    this.inv.getTransaction().put(TransactionKey.ITEM_SEARCH, itemName);
    this.inv.loadInterface(InterfaceType.AUCTION_VIEW_ITEM);
  }

  private void searchPlayer(String player) {
    final OfflinePlayer offlinePlayer = GlobalMarketChest.plugin.getServer().getOfflinePlayer(player);
    this.inv.getTransaction().put(TransactionKey.PLAYER, offlinePlayer);
    this.inv.loadInterface(InterfaceType.AUCTION_VIEW_BY_PLAYER);
  }
}
