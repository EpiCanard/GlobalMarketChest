package fr.epicanard.globalmarketchest.auctions;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
  private AuctionType type;
  @Getter
  private String playerStarter;
  @Getter
  private String playerEnder;
  @Getter
  private String start;
  @Getter
  private String end;
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
      this.state = StateAuction.getStateAuction(res.getInt("state"));
      this.type = AuctionType.getAuctionType(res.getInt("type"));
      this.playerStarter = res.getString("playerStarter");
      this.playerEnder = res.getString("playerEnder");
      this.start = res.getString("start");
      this.end = res.getString("end");
      this.group = res.getString("group");
    } catch (SQLException e) {
      GlobalMarketChest.plugin.getLogger().log(Level.WARNING, e.getMessage());
    }
  }

  public AuctionInfo(AuctionType type, Player owner, String group) {
    this.state = StateAuction.INPROGRESS;
    this.type = AuctionType.getAuctionType(type.getType());
    this.price = 0.0;
    this.playerStarter = owner.getUniqueId().toString();
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
      lore.add(String.format("&7%s : &2%s", LangUtils.get("Divers.State"), this.state.getLang()));
    if (config.getQuantity())
      lore.add(String.format("&7%s : &6%s", LangUtils.get("Divers.Quantity"), this.amount));
    if (config.getUnitPrice())
      lore.add(String.format("&7%s : &c%s", LangUtils.get("Divers.UnitPrice"), this.checkPrice(this.price)));
    if (config.getTotalPrice())
      lore.add(String.format("&7%s : &c%s", LangUtils.get("Divers.TotalPrice"), this.checkPrice(totalPrice)));
    if (config.getStarter())
      lore.add(String.format("&7%s : &9%s", LangUtils.get("Divers.Seller"),
        PlayerUtils.getOfflinePlayer(UUID.fromString(this.playerStarter)).getName()));
    if (config.getEnder())
      lore.add(String.format("&7%s : &9%s", LangUtils.get("Divers.Buyer"),
        PlayerUtils.getOfflinePlayer(UUID.fromString(this.playerEnder)).getName()));
    if (config.getStartDate())
      lore.add(String.format("&7%s : &6%s", LangUtils.get("Divers.StartDate"), this.start));
    if (config.getEndDate())
      lore.add(String.format("&7%s : &6%s", LangUtils.get("Divers.EndDate"), this.end));
      if (config.getExpiration())
      lore.add(String.format("&7%s : &6%s", LangUtils.get("Divers.Expiration"), this.end));
    lore.add("&6--------------");
    return lore;
  }
}
