package fr.epicanard.globalmarketchest.auctions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import lombok.Getter;

public class AuctionInfo {
  @Getter
  private int id;
  
  public AuctionInfo(ResultSet res) throws NullPointerException {
    if (res == null)
      throw new NullPointerException("Fail to get auction from database");
    try {
      this.id = res.getInt("id");
    } catch (SQLException e) {
      GlobalMarketChest.plugin.getLogger().log(Level.WARNING, e.getMessage());
    }
  }

  public AuctionInfo(int id) {
    this.id = id;
  }
}
