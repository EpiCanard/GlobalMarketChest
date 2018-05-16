package fr.epicanard.globalmarketchest.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.database.connections.DatabaseConnection;
import fr.epicanard.globalmarketchest.database.querybuilder.QueryBuilder;
import fr.epicanard.globalmarketchest.exceptions.ShopAlreadyExistException;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.WorldUtils;

public class ShopManager {
  private List<ShopInfo> shops = new ArrayList<ShopInfo>();

  /**
   * Update shops informations from Database informations
   */
  public void updateShops() {
    this.shops.clear();
    QueryBuilder builder = new QueryBuilder(DatabaseConnection.tableShops);
    ResultSet res = (ResultSet)builder.execute(builder.select());
    try {
      while (res.next())
        this.shops.add(new ShopInfo(res));
    } catch(SQLException e) {}
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
  public Integer createShop(Player owner, Location sign, Location other, int mask, String group) throws ShopAlreadyExistException {
    if (!this.shops.stream().allMatch(shop -> !WorldUtils.compareLocations(shop.getSignLocation(), sign)))
      throw new ShopAlreadyExistException(sign);

    QueryBuilder builder = new QueryBuilder(DatabaseConnection.tableShops);

    builder.addValue("owner", owner.getUniqueId().toString());
    builder.addValue("signLocation", WorldUtils.getStringFromLocation(sign));
    builder.addValue("otherLocation", WorldUtils.getStringFromLocation(other));
    builder.addValue("type", mask);
    builder.addValue("group", group);

    Integer res = (Integer)builder.execute(builder.insert());
    this.updateShops();
    return res;
  }


  /**
   * Delete shop at the specific location
   */
  public Boolean deleteShop(Location loc) {
    QueryBuilder builder = new QueryBuilder(DatabaseConnection.tableShops);
    Boolean ret = false;

    builder.addCondition("signLocation", WorldUtils.getStringFromLocation(loc));
    if ((Integer)builder.execute(builder.delete()) > 0)
      ret = true;
    this.shops.removeIf(shop -> WorldUtils.compareLocations(shop.getSignLocation(), loc));

    return ret;
  }
}
