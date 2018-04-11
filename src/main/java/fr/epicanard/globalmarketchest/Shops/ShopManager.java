package fr.epicanard.globalmarketchest.Shops;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.DatabaseConnections.DatabaseConnection;
import fr.epicanard.globalmarketchest.Utils.WorldUtils;

public class ShopManager {
  List<ShopInfo> shops = new ArrayList<ShopInfo>();

  /**
   * Update shops informations from Database informations
   */
  public void updateShops() {
    this.shops.clear();
    Connection co = GlobalMarketChest.plugin.getSqlConnection().getConnection();

    try {
      PreparedStatement prepared = co.prepareStatement("SELECT * FROM " + DatabaseConnection.tableShops);
      ResultSet res = prepared.executeQuery();
      while (res.next())
        this.shops.add(new ShopInfo(res));
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
  public ShopInfo getShop(int ID) {
    for (ShopInfo info : this.shops) {
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
    for (ShopInfo info : this.shops) {
      if (WorldUtils.compareLocations(info.getSignLocation(), location) || WorldUtils.compareLocations(info.getChestLocation(), location))
        return info;
    }
    return null;
  }
  
  public void createShop(ShopInfo shop) {
    Connection co = GlobalMarketChest.plugin.getSqlConnection().getConnection();

    try {
      PreparedStatement prepared = co.prepareStatement("INSERT INTO `" + DatabaseConnection.tableShops + 
          "` (`ownerID`, `signLocation`, `location2`, `adminshopOnly`, `itemFrame`, `itemStack`, `worldGroup`, `npcID`, `categoryID`, `holo`, `newAuctions`, `sellAll`, `appearance`, `defaultCategory`) " + 
          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
      
      prepared.setInt(1, 1);
      prepared.setString(2, WorldUtils.getStringFromLocation(shop.getSignLocation()));
      prepared.setString(3, WorldUtils.getStringFromLocation(shop.getChestLocation()));
      prepared.setString(7, shop.getWorldGroup());

      ResultSet res = prepared.executeQuery();

      GlobalMarketChest.plugin.getSqlConnection().closeRessources(res, prepared);
    } catch (SQLException e) {
      return;
    }

  }
}
