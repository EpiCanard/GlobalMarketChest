package fr.epicanard.globalmarketchest.shops;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.database.connections.DatabaseConnection;
import fr.epicanard.globalmarketchest.exceptions.ShopAlreadyExistException;
import fr.epicanard.globalmarketchest.utils.WorldUtils;

public class ShopManager {
  private List<ShopInfo> shops = new ArrayList<ShopInfo>();

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
    } catch (SQLException e) {}
  }

  public void listShops() {
    for (ShopInfo s: this.shops) {
      System.out.println(s.getId() + " - " + WorldUtils.getStringFromLocation(s.getSignLocation()));
    }
  }

  /**
   * Get a shop with his ID
   * @param ID
   * @return
   */
  public ShopInfo getShop(int id) {
    for (ShopInfo info : this.shops) {
      if (info.getId() == id)
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
      if (WorldUtils.compareLocations(info.getSignLocation(), location) || WorldUtils.compareLocations(info.getOtherLocation(), location))
        return info;
    }
    return null;
  }
  
  /**
   * Create a shop inside database and add it in list shops
   */
  public void createShop(Player owner, Location sign, Location other, int mask, String worldGroup) throws ShopAlreadyExistException {
    if (!this.shops.stream().allMatch(shop -> !WorldUtils.compareLocations(shop.getSignLocation(), sign)))
      throw new ShopAlreadyExistException(sign);

    Connection co = GlobalMarketChest.plugin.getSqlConnection().getConnection();

    try {
      PreparedStatement prepared = co.prepareStatement("INSERT INTO " + DatabaseConnection.tableShops + 
          " (owner, signLocation, otherLocation, type, worldGroup) VALUES (?, ?, ?, ?, ?)");
      
      prepared.setString(1, owner.getUniqueId().toString());
      prepared.setString(2, WorldUtils.getStringFromLocation(sign));
      prepared.setString(3, WorldUtils.getStringFromLocation(other));
      prepared.setInt(4, mask);
      prepared.setString(5, worldGroup);
      prepared.executeUpdate();

      prepared = co.prepareStatement("SELECT * FROM " + DatabaseConnection.tableShops + " WHERE signLocation = ?");
      prepared.setString(1, WorldUtils.getStringFromLocation(sign));
      ResultSet res = prepared.executeQuery();
      res.next();
      this.shops.add(new ShopInfo(res));

      GlobalMarketChest.plugin.getSqlConnection().closeRessources(res, prepared);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    GlobalMarketChest.plugin.getSqlConnection().getBackConnection(co);
  }

  /**
   * Delete shop at the specific location
   */
  public Boolean deleteShop(Location loc) {
    Connection co = GlobalMarketChest.plugin.getSqlConnection().getConnection();
    Boolean ret = false;

    try {
      PreparedStatement prepared = co.prepareStatement("DELETE FROM " + DatabaseConnection.tableShops + 
          " WHERE signLocation = ?");
      
      prepared.setString(1, WorldUtils.getStringFromLocation(loc));
      if (prepared.executeUpdate() > 0)
        ret = true;

      this.shops.removeIf(shop -> WorldUtils.compareLocations(shop.getSignLocation(), loc));

      GlobalMarketChest.plugin.getSqlConnection().closeRessources(null, prepared);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    GlobalMarketChest.plugin.getSqlConnection().getBackConnection(co);
    return ret;
  }
}
