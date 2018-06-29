package fr.epicanard.globalmarketchest.auctions;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
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
    this.state = StateAuction.getStateAuction(StateAuction.INPROGRESS.getState());
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
}
