package fr.epicanard.globalmarketchest.Players;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.DatabaseConnections.DatabaseConnection;
import fr.epicanard.globalmarketchest.Shops.ShopInfo;
import fr.epicanard.globalmarketchest.Utils.WorldUtils;

public class PlayerManager {
  List<ShopInfo> players = new ArrayList<ShopInfo>();

  /**
   * Update players list from Database informations
   */
  public void updatePlayers() {
    this.players.clear();
    Connection co = GlobalMarketChest.plugin.getSqlConnection().getConnection();

    try {
      PreparedStatement prepared = co.prepareStatement("SELECT * FROM " + DatabaseConnection.tablePlayers);
      ResultSet res = prepared.executeQuery();
      while (res.next())
        this.players.add(new ShopInfo(res));
      GlobalMarketChest.plugin.getSqlConnection().closeRessources(res, prepared);
    } catch (SQLException e) {
      return;
    }
  }

  /**
   * Get a shop with his ID
   * @param ID
   * @return
   */
  public ShopInfo getPlayer(int ID) {
    for (ShopInfo info : this.players) {
      if (info.getIDShop() == ID)
        return info;
    }
    return null;
  }

  /**
   * Get a shop with his location
   * @param location
   * @return
   */
  public ShopInfo getShop(Location location) {
    for (ShopInfo info : this.players) {
      if (WorldUtils.compareLocations(info.getSignLocation(), location) || WorldUtils.compareLocations(info.getChestLocation(), location))
        return info;
    }    
    return null;
  }

}
