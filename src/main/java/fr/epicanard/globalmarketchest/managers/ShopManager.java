package fr.epicanard.globalmarketchest.managers;

import fr.epicanard.globalmarketchest.database.DatabaseManager;
import fr.epicanard.globalmarketchest.database.connectors.DatabaseConnector;
import fr.epicanard.globalmarketchest.database.querybuilder.QueryExecutor;
import fr.epicanard.globalmarketchest.database.querybuilder.SqlConsumer;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.DeleteBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.InsertBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.SelectBuilder;
import fr.epicanard.globalmarketchest.exceptions.ShopAlreadyExistException;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;
import lombok.Getter;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class that handle all shops and communication with database
 */
public class ShopManager extends DatabaseManager {
  @Getter
  private List<ShopInfo> shops = new ArrayList<>();

  public ShopManager() {
    super(DatabaseConnector.tableShops);
  }

  /**
   * Remove every metadata from shop and clear the shops list
   */
  private void resetShopList() {
    this.shops.stream().filter(ShopInfo::getExists).forEach(ShopInfo::removeMetadata);
    this.shops.clear();
  }

  /**
   * Update shops informations from Database informations
   */
  public void updateShops() {
    this.resetShopList();

    final SelectBuilder builder = select();
    QueryExecutor.of().execute(builder, res -> {
      while (res.next()) {
        final ShopInfo shop = new ShopInfo(res);
        if (shop.getSignLocation() != null && shop.getSignLocation().getWorld() != null) {
          shop.addMetadata();
        } else {
          shop.setExists(false);
        }
        this.shops.add(shop);
      }
    }, Exception::printStackTrace);
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
   * @param owner Owner of the shop
   * @param sign  Location of sign placed
   * @param other Linked location to sign
   * @param mask  Shop type
   * @param group Group of shop to link auctions
   * @return Return shop id created
   */
  private Integer createShop(String owner, Location sign, Location other, int mask, String group) throws ShopAlreadyExistException {
    if (this.shops.stream().anyMatch(shop -> WorldUtils.compareLocations(shop.getSignLocation(), sign)))
      throw new ShopAlreadyExistException(sign);

    final InsertBuilder builder = insert()
        .addValue("owner", owner)
        .addValue("signLocation", WorldUtils.getStringFromLocation(sign))
        .addValue("otherLocation", WorldUtils.getStringFromLocation(other))
        .addValue("type", mask)
        .addValue("group", group);

    final AtomicInteger id = new AtomicInteger(-1);
    final SqlConsumer<ResultSet> cs = res -> {
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

    final DeleteBuilder builder = delete()
        .addCondition("id", shop.getId());

    return QueryExecutor.of().execute(builder);
  }
}
