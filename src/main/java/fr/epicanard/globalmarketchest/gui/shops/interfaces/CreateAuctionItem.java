package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.gui.InterfacesLoader;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.gui.shops.toggler.SingleToggler;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class CreateAuctionItem extends ShopInterface {
  public CreateAuctionItem(InventoryGUI inv) {
    super(inv);
    this.isTemp = true;
    this.togglers.put(53, new SingleToggler(inv.getInv(), 53, inv.getInv().getItem(53), Utils.getBackground()));
    this.actions.put(22, i -> this.unsetItem());
    this.actions.put(0, new PreviousInterface());
    this.actions.put(48, i -> this.defineMaxInOne());
    this.actions.put(50, i -> this.defineMaxRepeat());
    this.actions.put(53, new NextInterface("CreateAuctionPrice", this::checkItem));
  }

  @Override
  public void load() {
    super.load();

    ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMPITEM);
    if (item != null)
      this.inv.getInv().setItem(22, item);
    else
      this.unsetItem();
  }

  /**
   * Set the item in dropzone when drop
   *
   * @param item ItemStack to set in drop zone
   */
  private void setItem(ItemStack item) {
    AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);

    auction.setAmount(item.getAmount());
    auction.setItemStack(item);
    this.inv.getTransaction().put(TransactionKey.AUCTIONNUMBER, 1);
    this.inv.getTransaction().put(TransactionKey.TEMPITEM, item.clone());
    this.inv.getTransaction().put(TransactionKey.AUCTIONAMOUNT, item.getAmount());
    this.updateItem();
    this.togglers.forEach((k, v) -> v.set());
  }

  /**
   * Remove the item from drop zone
   */
  private void unsetItem() {
    this.inv.getTransaction().remove(TransactionKey.TEMPITEM);
    this.inv.getTransaction().remove(TransactionKey.AUCTIONAMOUNT);
    ItemStack[] items = InterfacesLoader.getInstance().getInterface("CreateAuctionItem");
    this.inv.getInv().setItem(22, items[22]);
    this.togglers.forEach((k, v) -> v.unset());
  }

  /**
   * Check if TEMPITEM is set (item dropped in interface)
   *
   * @return false if TEMPITEM is not set else true
   */
  private Boolean checkItem() {
    ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMPITEM);
    if (item != null && GlobalMarketChest.plugin.getConfigLoader().getConfig().getBoolean("Auctions.UseLastPrice", true)) {
      AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);
      GlobalMarketChest.plugin.auctionManager.getLastPrice(auction, price -> auction.setPrice(price));
    }
    return (item != null);
  }

  /**
   * Get lore with quantity and price for current auction item
   *
   * @return the lore completed
   */
  private void updateItem() {
    ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMPITEM);
    AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);
    List<String> lore = new ArrayList<>();

    lore.add("&7" + LangUtils.get("Divers.Quantity") + " : &6" + auction.getAmount());
    lore.add("&7" + LangUtils.get("Divers.AuctionNumber") + " : &6" + this.inv.getTransactionValue(TransactionKey.AUCTIONNUMBER));
    this.inv.getInv().setItem(22, ItemStackUtils.setItemStackLore(item.clone(), lore));
  }

  private void defineMaxInOne() {
    ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMPITEM);
    AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);

    if (item == null || auction == null)
      return;
    this.inv.getTransaction().put(TransactionKey.AUCTIONNUMBER, 1);
    ItemStack[] items = this.inv.getPlayer().getInventory().getContents();
    Integer max = Arrays.asList(items).stream().filter(it -> it != null && it.isSimilar(item)).reduce(0,
        (res, val) -> res + val.getAmount(), (s1, s2) -> s1 + s2);
    item.setAmount(ItemStackUtils.getMaxStack(item, max));
    auction.setItemStack(item);
    auction.setAmount(max);
    this.updateItem();
  }

  private void defineMaxRepeat() {
    final ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMPITEM);
    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);
    if (item == null || auction == null)
      return;

    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);
    final Integer maxAuctions = GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt("Auctions.MaxAuctionByPlayer");
    final ItemStack[] items = this.inv.getPlayer().getInventory().getContents();
    final Integer max = Arrays.asList(items).stream().filter(it -> it != null && it.isSimilar(item)).reduce(0,
        (res, val) -> res + val.getAmount(), (s1, s2) -> s1 + s2);
    auction.setAmount(this.inv.getTransactionValue(TransactionKey.AUCTIONAMOUNT));
    item.setAmount(auction.getAmount());
    auction.setItemStack(item);
    final Integer auctionNumber = max / auction.getAmount();

    GlobalMarketChest.plugin.auctionManager.getAuctionNumber(shop.getGroup(), inv.getPlayer(), num -> {
      this.inv.getTransaction().put(TransactionKey.AUCTIONNUMBER, (num + auctionNumber > maxAuctions) ? maxAuctions - num : auctionNumber);
      this.updateItem();
    });
  }

  /**
   * Called when a mouse drop event is done inside inventory
   *
   * @param event
   */
  @Override
  public void onDrop(InventoryClickEvent event, InventoryGUI inv) {
    ItemStack item;
    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
      item = event.getCurrentItem();
    else {
      item = event.getCursor();
      event.getWhoClicked().setItemOnCursor(null);
      event.getWhoClicked().getInventory().addItem(item);
    }
    this.setItem(item);
  }

  @Override
  public void destroy() {
    super.destroy();
    this.unsetItem();
    this.inv.getTransaction().remove(TransactionKey.TEMPITEM);
    this.inv.getTransaction().remove(TransactionKey.AUCTIONINFO);
    this.inv.getTransaction().remove(TransactionKey.AUCTIONNUMBER);
    this.inv.getTransaction().remove(TransactionKey.AUCTIONAMOUNT);
  }
}
