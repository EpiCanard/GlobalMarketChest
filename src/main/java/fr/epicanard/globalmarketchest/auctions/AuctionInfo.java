package fr.epicanard.globalmarketchest.auctions;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import lombok.Getter;
import lombok.Setter;

public class AuctionInfo {
  @Getter
  private Integer id;
  @Getter
  private String itemStack;
  @Getter
  private Short damage;
  @Getter
  private String itemMeta;
  @Getter @Setter
  private Integer amount;
  @Getter @Setter
  private Double price;
  @Getter
  private StateAuction state;
  @Getter
  private Boolean ended;
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
    try {
      this.id = res.getInt("id");
      this.itemStack = res.getString("itemStack");
      this.damage = res.getShort("damage");
      this.itemMeta = res.getString("itemMeta");
      this.amount = res.getInt("amount");
      this.price = res.getDouble("price");
      this.ended = res.getBoolean("ended");
      this.type = AuctionType.getAuctionType(res.getInt("type"));
      this.playerStarter = res.getString("playerStarter");
      this.playerEnder = res.getString("playerEnder");
      this.start = res.getTimestamp("start");
      this.end = res.getTimestamp("end");
      this.group = res.getString("group");
      this.state = StateAuction.getStateAuction(this);
    } catch (SQLException e) {
      GlobalMarketChest.plugin.getLogger().log(Level.WARNING, e.getMessage());
    }
  }

  public AuctionInfo(AuctionType type, Player owner, String group) {
    this.state = StateAuction.INPROGRESS;
    this.type = AuctionType.getAuctionType(type.getType());
    this.price = GlobalMarketChest.plugin.getConfigLoader().getConfig().getDouble("Auctions.DefaultPrice", 0.0);
    this.playerStarter = owner.getUniqueId().toString();
    this.ended = false;
    this.group = group;
  }

  public Double getTotalPrice() {
    return BigDecimal.valueOf(this.price).multiply(BigDecimal.valueOf(this.amount)).doubleValue();
  }

  public void setItemStack(ItemStack item) {
    this.itemStack = ItemStackUtils.getMinecraftKey(item);
    this.damage = item.getDurability();
    this.itemMeta = DatabaseUtils.serialize(item);
  }

  private String checkPrice(double price) {
    if (price == 0.0)
      return "&2" + LangUtils.get("Divers.Free");
    return Double.toString(price);
  }

  public ItemStack getRealItemStack() {
    ItemStack item = DatabaseUtils.deserialize(this.itemMeta);
    item.setAmount(this.amount);
    return item;
  }

  private void addLore(List<String> lore, String key, String color, String value) {
    lore.add(String.format("&7%s : %s%s", LangUtils.get("Divers." + key), color, value));
  }

  /**
   * Build and return lore for current auction
   *
   * @param status
   * @return the lore
   */
  public List<String> getLore(AuctionLoreConfig config) {
    List<String> lore = new ArrayList<>();

    double totalPrice = BigDecimal.valueOf(this.price).multiply(BigDecimal.valueOf(this.amount)).doubleValue();
    lore.add("&6--------------");
    if (config.getState())
      this.addLore(lore, "State", "&2", this.state.getLang());
    if (config.getQuantity())
      this.addLore(lore, "Quantity", "&6", this.amount.toString());
    if (config.getUnitPrice())
      this.addLore(lore, "UnitPrice", "&c", this.checkPrice(this.price));
    if (config.getTotalPrice())
      this.addLore(lore, "TotalPrice", "&c", this.checkPrice(totalPrice));
    if (config.getStarter())
      this.addLore(lore, "Seller", "&9", PlayerUtils.getPlayerName(this.playerStarter));
    if (config.getEnder())
      this.addLore(lore, "Buyer", "&9", PlayerUtils.getPlayerName(this.playerEnder));
    if (config.getStarted())
      this.addLore(lore, "Started", "&6",
        DatabaseUtils.getExpirationString(this.start, DatabaseUtils.getTimestamp(), false));
    if (config.getEnded())
      this.addLore(lore, "Ended", "&6",
        DatabaseUtils.getExpirationString(this.end, DatabaseUtils.getTimestamp(), false));
    if (config.getExpire()) {
      String path = (this.end.getTime() < DatabaseUtils.getTimestamp().getTime()) ? "Expired" : "ExpireIn";
      this.addLore(lore, path, "&6",
        DatabaseUtils.getExpirationString(this.end, DatabaseUtils.getTimestamp(), false));
    }
    lore.add("&6--------------");
    return lore;
  }
}
