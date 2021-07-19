package fr.epicanard.globalmarketchest.auctions;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.configuration.PriceLimit;
import fr.epicanard.globalmarketchest.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static fr.epicanard.globalmarketchest.utils.DatabaseUtils.getField;
import static fr.epicanard.globalmarketchest.utils.EconomyUtils.format;

public class AuctionInfo {
  @Getter
  private Integer id;
  @Getter
  private String itemStack;
  @Getter @Setter
  private String itemMeta;
  @Getter @Setter
  private Integer amount;
  @Getter @Setter
  private Double price;
  @Getter
  private StatusAuction status;
  @Getter
  private AuctionType type;
  @Getter
  private String playerStarter;
  @Getter
  private String playerEnder;
  @Getter
  private Timestamp start;
  @Getter
  private Timestamp end;
  @Getter
  private String group;

  public AuctionInfo(ResultSet res) {
    if (res == null)
      throw new NullPointerException("Fail to get auction from database");
    this.id = getField("id", res::getInt);
    this.itemStack = getField("itemStack", res::getString);
    this.itemMeta = getField("itemMeta", res::getString);
    this.amount = getField("amount", res::getInt);
    this.price = getField("price", res::getDouble);
    this.type = AuctionType.getAuctionType(getField("type", res::getInt));
    this.playerStarter = getField("playerStarter", res::getString);
    this.playerEnder = getField("playerEnder", res::getString);
    this.start = getField("start", res::getTimestamp);
    this.end = getField("end", res::getTimestamp);
    this.status = this.defineStatus(getField("status", res::getInt), this.end);
    this.group = getField("group", res::getString);

    if (this.itemMeta == null && this.itemStack != null) {
      this.itemMeta = DatabaseUtils.serialize(ItemStackUtils.getItemStack(this.itemStack));
    }
  }

  public AuctionInfo(AuctionType type, Player owner, String group) {
    this.status = StatusAuction.IN_PROGRESS;
    this.type = AuctionType.getAuctionType(type.getType());
    this.price = ConfigUtils.getDouble("Options.DefaultPrice", 0.0);
    this.playerStarter = owner.getUniqueId().toString();
    this.group = group;
  }

  public AuctionInfo(ItemStack item) {
    this.setItemStack(item);
  }

  /**
   * Get total price of auction
   *
   * @return Total price
   */
  public Double getTotalPrice() {
    return BigDecimal.valueOf(this.price).multiply(BigDecimal.valueOf(this.amount)).doubleValue();
  }

  /**
   * Get final total price of auction after tax
   *
   * @return Final total price after tax
   */
  public Double getTaxedPrice() {
    //Check if the tax is a valid multiplier or bad things may happen if configured outside the proper bounds
    double taxMultiplier = 1 - ConfigUtils.getDouble("Options.ServerTax", 0);
    if (taxMultiplier < 0 || taxMultiplier > 1) {
      taxMultiplier = 0.0;
    }
    return BigDecimal.valueOf(getTotalPrice()).multiply(BigDecimal.valueOf(taxMultiplier)).doubleValue();
  }

  /**
   * Get taxed amount
   *
   * @return Total tax amount
   */
  public Double getTaxAmount() {
    //Check if the tax is a valid multiplier or bad things may happen if configured outside the propper bounds
    double taxMultiplier = ConfigUtils.getDouble("Options.ServerTax", 0);
    if (taxMultiplier < 0 || taxMultiplier > 1) {
      taxMultiplier = 0.0;
    }
    return BigDecimal.valueOf(getTotalPrice()).multiply(BigDecimal.valueOf(taxMultiplier)).doubleValue();
  }

  /**
   * Set itemstack inside auction
   *
   * @param item Item to set inside auction
   */
  public void setItemStack(ItemStack item) {
    ItemStack it = item.clone();
    it.setAmount(1);
    this.itemStack = ItemStackUtils.getMinecraftKey(it);
    this.itemMeta = DatabaseUtils.serialize(it);
  }

  /**
   * Get the final array of itemstack the auction contains
   *
   * @return List of itemstack
   */
  public ItemStack[] getRealItemStack() {
    final List<ItemStack> items = new ArrayList<>();

    ItemStack item = DatabaseUtils.deserialize(this.itemMeta);
    int amount = this.amount;
    int stackSize = item.getMaxStackSize();
    while (amount > 0) {
      ItemStack it = item.clone();
      it.setAmount(ItemStackUtils.getMaxStack(it, amount));
      items.add(it);
      amount -= stackSize;
    }
    return items.toArray(new ItemStack[0]);
  }


  /**
   * Build and return lore for current auction
   *
   * @param config Config used to define which infos must be displayed
   * @return the lore
   */
  public List<String> getLore(final AuctionLoreConfig config) {
    final List<String> lore = this.buildBaseLore(config);
    lore.add(GlobalMarketChest.plugin.getCatHandler().getDisplayCategory(this.itemStack));
    return lore;
  }

  /**
   * Build and return lore for current auction
   *
   * @param config Config used to define which infos must be displayed
   * @return the lore
   */
  public List<String> getLore(final AuctionLoreConfig config, final PriceLimit priceLimit) {
    final List<String> lore = this.buildBaseLore(config);
    if (priceLimit != null) {
      if (priceLimit.Min > 0) {
        this.addLore(lore, "MinPrice", "&6", format(priceLimit.Min));
      }
      if (priceLimit.Max > 0) {
        this.addLore(lore, "MaxPrice", "&6", format(priceLimit.Max));
      }
    }
    lore.add(GlobalMarketChest.plugin.getCatHandler().getDisplayCategory(this.itemStack));
    return lore;
  }

  /**
   * Build and return the base lore for current auction
   *
   * @param config Config used to define which infos must be displayed
   * @return the lore
   */
  private List<String> buildBaseLore(final AuctionLoreConfig config) {
    final List<String> lore = new ArrayList<>();

    double totalPrice = BigDecimal.valueOf(this.price).multiply(BigDecimal.valueOf(this.amount)).doubleValue();
    if (config.frame())
      lore.add("&6--------------");
    if (config.state())
      this.addLore(lore, "State", "&2", this.status.getLang());
    if (config.quantity())
      this.addLore(lore, "Quantity", "&6", this.amount.toString());
    if (config.unitPrice())
      this.addLore(lore, "UnitPrice", "&c", this.checkPrice(this.price));
    if (config.totalPrice())
      this.addLore(lore, "TotalPrice", "&c", this.checkPrice(totalPrice));
    if (config.starter() && !ConfigUtils.getBoolean("Options.Anonymous.Seller", false))
      this.addLore(lore, "Seller", "&9", PlayerUtils.getPlayerName(this.playerStarter));
    if (config.ender() && !ConfigUtils.getBoolean("Options.Anonymous.Buyer", false))
      this.addLore(lore, "Buyer", "&9", PlayerUtils.getPlayerName(this.playerEnder));
    if (config.started())
      this.addLore(lore, "Started", "&6",
          DatabaseUtils.getExpirationString(this.start, DatabaseUtils.getTimestamp(), false));
    if (config.ended())
      this.addLore(lore, "Ended", "&6",
          DatabaseUtils.getExpirationString(this.end, DatabaseUtils.getTimestamp(), false));
    if (config.expire()) {
      String path = (this.end.getTime() < DatabaseUtils.getTimestamp().getTime()) ? "Expired" : "ExpireIn";
      this.addLore(lore, path, "&6",
          DatabaseUtils.getExpirationString(this.end, DatabaseUtils.getTimestamp(), false));
    }
    if (config.canceled() && !this.playerStarter.equals(this.playerEnder))
      this.addLore(lore, "CanceledBy", "&c", PlayerUtils.getPlayerName(this.playerEnder));
    if (config.frame())
      lore.add("&6--------------");
    return lore;
  }


  /**
   * Add a formatted lore inside a list of lore
   *
   * @param lore  List of lore
   * @param key   Key to add
   * @param color Color to use for value
   * @param value Value to use
   */
  private void addLore(List<String> lore, String key, String color, String value) {
    lore.add(String.format("&7%s : %s%s", LangUtils.get("Divers." + key), color, value));
  }

  /**
   * Get string price of auctions, if free display "Free"
   *
   * @param price Price in double value
   * @return String price
   */
  private String checkPrice(double price) {
    if (price == 0.0)
      return "&2" + LangUtils.get("Divers.Free");
    return format(price);
  }

  /**
   * Define real status depending of registered status and expiration date
   *
   * @param status  Status registered
   * @param endTime Expiration date of auction
   * @return The correct status
   */
  private StatusAuction defineStatus(Integer status, Timestamp endTime) {
    final StatusAuction statusAuction = StatusAuction.getStatusAuction(status);
    if (statusAuction.equals(StatusAuction.IN_PROGRESS) && endTime != null
        && endTime.getTime() < DatabaseUtils.getTimestamp().getTime()) {
      return StatusAuction.EXPIRED;
    }
    return statusAuction;
  }
}
