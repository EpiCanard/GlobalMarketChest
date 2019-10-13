package fr.epicanard.globalmarketchest.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.bukkit.Location;

import fr.epicanard.globalmarketchest.database.connectors.DatabaseConnector;
import fr.epicanard.globalmarketchest.database.querybuilder.QueryExecutor;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.DeleteBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.InsertBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.SelectBuilder;
import fr.epicanard.globalmarketchest.exceptions.ShopAlreadyExistException;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;
import lombok.Getter;

/**
 * Class that handle all shops and communication with database
 */
public class ShopManager {
  @Getter
  private List<ShopInfo> shops = new ArrayList<ShopInfo>();

  /**
   * Remove every metadata from shop and clear the shops list
   */
  public void resetShopList() {
    this.shops.stream().filter(shop -> shop.getExists()).forEach(shop -> shop.removeMetadata());
    this.shops.clear();
  }

  /**
   * Update shops informations from Database informations
   */
  public void updateShops() {
    this.resetShopList();

    SelectBuilder builder = new SelectBuilder(DatabaseConnector.tableShops);
    QueryExecutor.of().execute(builder, res -> {
      try {
        while (res.next()) {
          ShopInfo shop = new ShopInfo(res);
          if (shop.getSignLocation() != null && shop.getSignLocation().getWorld() != null) {
            shop.addMetadata();
          } else {
            shop.setExists(false);
          }
          this.shops.add(shop);
        }
      } catch(SQLException e) {e.printStackTrace();}
    });
  }

  /**
   * Get a shop with his ID
   * 
   * @param id id of the shop
   * @return Return the shop with this id
   */
  public ShopInfo getShop(int id) {
    for (ShopInfo info : this.shops) {
      if (info.getId() == id)
        return info;
    }
    return null;
  }
  
  /**
   * Create a shop inside database and add it in list shops
   * 
   * @param owner
   * @param sign
   * @param other
   * @param mask
   * @param group
   * @return Return shop id created
   */
  public Integer createShop(String owner, Location sign, Location other, int mask, String group) throws ShopAlreadyExistException {
    if (!this.shops.stream().allMatch(shop -> !WorldUtils.compareLocations(shop.getSignLocation(), sign)))
      throw new ShopAlreadyExistException(sign);

    InsertBuilder builder = new InsertBuilder(DatabaseConnector.tableShops);

    builder.addValue("owner", owner);
    builder.addValue("signLocation", WorldUtils.getStringFromLocation(sign));
    builder.addValue("otherLocation", WorldUtils.getStringFromLocation(other));
    builder.addValue("type", mask);
    builder.addValue("group", group);

    AtomicInteger id = new AtomicInteger(-1);
    Consumer<ResultSet> cs = res -> {
      id.set(DatabaseUtils.getId(res));
    };
    if (QueryExecutor.of().execute(builder, cs))
      this.updateShops();
    return id.get();
  }

  /**
   * Create a shop inside database and add it in list shops
   * 
   * @param shop Info about a shop
   * @return Return Shop id created
   */
  public Integer createShop(ShopInfo shop) throws ShopAlreadyExistException {
    return this.createShop(shop.getOwner(), shop.getSignLocation(), shop.getOtherLocation(), shop.getType(), shop.getGroup());
  }


  /**
   * Delete shop at the specific location
   * 
   * @param shop Shop to delete
   * @return Return true if succeed else false
   */
  public Boolean deleteShop(ShopInfo shop) {
    if (shop == null)
      return false;

    this.shops.removeIf(s -> s.getId() == shop.getId());
    shop.removeMetadata();

    DeleteBuilder builder = new DeleteBuilder(DatabaseConnector.tableShops);

    builder.addCondition("id", shop.getId());
    return QueryExecutor.of().execute(builder);
  }
}
