package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import com.google.common.collect.ImmutableMap;
import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.actions.ReturnBack;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.ShopInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static fr.epicanard.globalmarketchest.utils.LangUtils.formatString;
import static fr.epicanard.globalmarketchest.utils.LangUtils.getOrElse;
import static fr.epicanard.globalmarketchest.utils.Utils.toList;
import static java.util.Collections.singletonMap;

public class CreateAuctionPrice extends ShopInterface {
  private List<Double> prices;
  private List<String> priceItems;
  private Boolean dynamicFreePos;
  private Double advicePrice;

  public CreateAuctionPrice(InventoryGUI inv) {
    super(inv);
    this.isTemp = true;
    this.actions.put(0, new PreviousInterface());
    this.actions.put(49, i -> this.setPrice(this.advicePrice, true));
    this.actions.put(53, this::createAuction);

    final YamlConfiguration config = ConfigUtils.get();

    this.prices = config.getDoubleList("Price.Ranges");
    this.prices = this.prices.subList(0, Utils.getIndex(9, this.prices.size(), true));
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
    final ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMP_ITEM);

    final List<String> lore = this.getLore();

    int i = 0;
    for (i = 0; i < this.prices.size(); i++) {
      ItemStack priceItem = ItemStackUtils.getItemStack(this.priceItems.get(Utils.getIndex(i, this.priceItems.size(), false)));
      this.inv.getInv().setItem(18 + i, ItemStackUtils.setItemStackMeta(priceItem, "&a+ " + prices.get(i), lore));
      this.inv.getInv().setItem(27 + i, ItemStackUtils.setItemStackMeta(priceItem.clone(), "&c- " + prices.get(i), lore));
    }
    this.inv.getInv().setItem((i == 9 || !this.dynamicFreePos) ? 40 : 27 + i, Utils.getButton("FreePrice"));
    this.setIcon(ItemStackUtils.setItemStackLore(item.clone(), lore));
    this.setAveragePrice();
  }

  /**
   * Add the price gave in parameter to the current price and update interface
   *
   * @param price price to set or add
   * @param set   Define if price must be set or added
   */
  private void setPrice(double price, Boolean set) {
    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTION_INFO);

    if (!set) {
      final BigDecimal dec = BigDecimal.valueOf(auction.getPrice()).add(BigDecimal.valueOf(price));
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
    final ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMP_ITEM);
    final List<String> lore = this.getLore();
    final Inventory inventory = this.inv.getInv();

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
    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTION_INFO);
    final Integer auctionNumber = this.inv.getTransactionValue(TransactionKey.AUCTION_NUMBER);

    final List<String> lore = auction.getLore(AuctionLoreConfig.SELECTPRICE);
    if (auctionNumber > 1)
      lore.set(0, String.format("%s &ax&9%s", lore.get(0), auctionNumber));
    return lore;
  }

  /**
   * Create auction inside database and leave interface
   *
   * @param i InventoryGui used
   */
  private void createAuction(InventoryGUI i) {
    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTION_INFO);
    final Integer auctionNumber = this.inv.getTransactionValue(TransactionKey.AUCTION_NUMBER);
    final ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMP_ITEM);
    final PlayerInventory playerInv = i.getPlayer().getInventory();

    if (!playerInv.containsAtLeast(item, auction.getAmount() * auctionNumber)) {
      this.inv.getWarn().warn("MissingItems", 40);
      return;
    }
    final Integer expirationDays = i.getPlayerRankProperties().getNumberDaysExpiration();
    final Boolean ret = GlobalMarketChest.plugin.auctionManager.createAuction(auction, auctionNumber, expirationDays);
    if (!ret) {
      this.inv.getWarn().warn("FailCreateAuction", 40);
      return;
    }
    Integer totalAmount = auction.getAmount() * auctionNumber;
    final ItemStack it = item.clone();
    final Integer stackSize = it.getMaxStackSize();
    while (totalAmount > 0) {
      it.setAmount(ItemStackUtils.getMaxStack(it, totalAmount));
      playerInv.removeItem(it);
      totalAmount -= stackSize;
    }

    this.broadcastAuctionCreation(i.getPlayer(), auction, auctionNumber, item);

    ReturnBack.execute(null, i);
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  /**
   * Define the advice average price and update interface
   */
  private void setAveragePrice() {
    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTION_INFO);
    final Integer days = ConfigUtils.getInt("Options.AdvicePrice.Days", 30);
    final String analyze = ConfigUtils.getString("Options.AdvicePrice.Analyze", "all");

    GlobalMarketChest.plugin.auctionManager.getAveragePriceItem(auction.getItemMeta(), auction.getGroup(), days, analyze, price -> {

    GlobalMarketChest.plugin.auctionManager.getAveragePriceItem(auction, days, defaultPrice, price -> {
      this.advicePrice = price;
      final ItemStack item = this.inv.getInv().getItem(49);
      final String formattedPrice = EconomyUtils.format(price);
      final String description = formatString(
          getOrElse("Buttons.AdvicePriceInfo.Description", "{advicePrice}"),
          singletonMap("advicePrice", formattedPrice)
      );
      this.inv.getInv().setItem(49, ItemStackUtils.setItemStackLore(item, toList(description)));
    });
  }

  /**
   * Broadcast a message inside current world to inform about the creation of this auction
   *
   * @param owner         Owner of auction
   * @param auction       Auction created
   * @param auctionNumber Number of time si auction is repeated
   * @param item          Item sold
   */
  private void broadcastAuctionCreation(final Player owner, final AuctionInfo auction, final int auctionNumber, final ItemStack item) {
    PlayerUtils.sendMessageConfig(owner, "InfoMessages.CreateAuction");

    if (!ConfigUtils.getBoolean("Options.Broadcast.CreationInsideWorld", true)) {
      return;
    }

    final Map<String, Object> mapping = ImmutableMap.of(
        "seller", owner.getName(),
        "auctionNumber", auctionNumber,
        "quantity", auction.getAmount(),
        "itemName", ItemStackUtils.getItemStackDisplayName(item),
        "price", EconomyUtils.format(auction.getTotalPrice())
    );

    final String message = LangUtils.format("InfoMessages.AuctionCreated", mapping);
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    WorldUtils.broadcast(shop.getSignLocation().getWorld(), message, Arrays.asList(owner));
  }
}
