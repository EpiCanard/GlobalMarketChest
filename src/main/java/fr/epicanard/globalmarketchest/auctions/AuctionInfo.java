package fr.epicanard.globalmarketchest.auctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import lombok.Getter;

public class AuctionInfo {
  @Getter
  private Integer id;
  @Getter
  private String itemStack;
  @Getter
  private String itemMeta;
  @Getter
  private Integer amount;
  @Getter
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
  
  public AuctionInfo(ResultSet res) throws NullPointerException {
    if (res == null)
      throw new NullPointerException("Fail to get auction from database");
    try {
      this.id = res.getInt("id");
      this.itemStack = res.getString("itemStack");
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
    this.playerStarter = owner.getUniqueId().toString();
    this.group = group;
  }
}
