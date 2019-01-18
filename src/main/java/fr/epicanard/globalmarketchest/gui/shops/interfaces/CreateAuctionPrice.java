package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.math.BigDecimal;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.actions.ReturnBack;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class CreateAuctionPrice extends ShopInterface {
  private List<Double> prices;
  private List<String> priceItems;
  private Boolean dynamicFreePos;

  public CreateAuctionPrice(InventoryGUI inv) {
    super(inv);
    this.isTemp = true;
    this.actions.put(0, new PreviousInterface());
    this.actions.put(53, this::createAuction);

    YamlConfiguration config = GlobalMarketChest.plugin.getConfigLoader().getConfig();

    this.prices = config.getDoubleList("Price.Ranges");
    this.prices = this.prices.subList(0, Utils.getIndex(8, this.prices.size()) + 1);
    this.priceItems = config.getStringList("Price.Items");
    this.dynamicFreePos = config.getBoolean("Price.DynamicFreePosition", true);

    int i = 0;
    for (i = 0; i < this.prices.size(); i++) {
      final int j = i;
      this.actions.put(18 + i, iv -> this.setPrice(this.prices.get(j), false));
      this.actions.put(27 + i, iv -> this.setPrice(-1 * this.prices.get(j), false));
    }
    this.actions.put((i == 9 || !this.dynamicFreePos) ? 40 : 27 + i, iv -> this.setPrice(0, true));
  }

  @Override
  public void load() {
    super.load();
    ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMPITEM);

    List<String> lore = this.getLore();

    int i = 0;
    for (i = 0; i < this.prices.size(); i++) {
      ItemStack priceItem = ItemStackUtils.getItemStack(this.priceItems.get(Utils.getIndex(i, this.priceItems.size())));
      this.inv.getInv().setItem(18 + i, ItemStackUtils.setItemStackMeta(priceItem, "&a+ " + prices.get(i), lore));
      this.inv.getInv().setItem(27 + i, ItemStackUtils.setItemStackMeta(priceItem.clone(), "&c- " + prices.get(i), lore));
    }
    this.inv.getInv().setItem((i == 9 || !this.dynamicFreePos) ? 40 : 27 + i, Utils.getButton("FreePrice"));
    this.setIcon(ItemStackUtils.setItemStackLore(item.clone(), lore));
  }

  /**
   * Add the price gave in parameter to the current price and update interface
   *
   * @param price price to set or add
   * @param set Define if price must be set or added
   */
  private void setPrice(double price, Boolean set) {
    AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);

    if (!set) {
      BigDecimal dec = BigDecimal.valueOf(auction.getPrice()).add(BigDecimal.valueOf(price));
      auction.setPrice((dec.doubleValue() < 0.0) ? 0 : dec.doubleValue());
    } else {
      auction.setPrice(price);
    }
    this.updatePrice();
  }

  /**
   * Update lore of buttons with auction price
   */
  private void updatePrice() {
    ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMPITEM);
    List<String> lore = this.getLore();
    Inventory inventory = this.inv.getInv();

    for (int i = 0; i < this.prices.size(); i++) {
      inventory.setItem(18 + i, ItemStackUtils.setItemStackLore(inventory.getItem(18 + i), lore));
      inventory.setItem(27 + i, ItemStackUtils.setItemStackLore(inventory.getItem(27 + i), lore));
    }
    this.setIcon(ItemStackUtils.setItemStackLore(item.clone(), lore));
  }

  /**
   * Get lore with quantity and price for current auction item
   *
   * @return the lore completed
   */
  private List<String> getLore() {
    AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);
    Integer auctionNumber = this.inv.getTransactionValue(TransactionKey.AUCTIONNUMBER);

    List<String> lore = auction.getLore(AuctionLoreConfig.SELECTPRICE);
    if (auctionNumber > 1)
      lore.set(0,String.format("%s &ax&9%s", lore.get(0), auctionNumber));
    return lore;
  }

  /**
   * Create auction inside database and leave interface
   *
   * @param i InventoryGui used
   */
  private void createAuction(InventoryGUI i) {
    AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);
    Integer auctionNumber = this.inv.getTransactionValue(TransactionKey.AUCTIONNUMBER);
    ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMPITEM);
    PlayerInventory playerInv = i.getPlayer().getInventory();

    if (!playerInv.containsAtLeast(item, auction.getAmount() * auctionNumber)) {
      this.inv.getWarn().warn("MissingItems", 49);
      return;
    }
    Boolean ret = GlobalMarketChest.plugin.auctionManager.createAuction(auction, auctionNumber);
    if (!ret) {
      this.inv.getWarn().warn("FailCreateAuction", 49);
      return;
    }
    Integer totalAmount = auction.getAmount() * auctionNumber;
    ItemStack it = item.clone();
    Integer stackSize = it.getMaxStackSize();
    while (totalAmount > 0) {
      it.setAmount(ItemStackUtils.getMaxStack(it, totalAmount));
      playerInv.removeItem(it);
      totalAmount -= stackSize;
    }
    PlayerUtils.sendMessageConfig(i.getPlayer(), "InfoMessages.CreateAuction");
    ReturnBack.execute(null, i);
  }

  @Override
  public void destroy() {
    super.destroy();
  }
}
