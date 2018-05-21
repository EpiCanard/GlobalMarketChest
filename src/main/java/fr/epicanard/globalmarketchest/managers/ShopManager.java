package fr.epicanard.globalmarketchest.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.database.connections.DatabaseConnection;
import fr.epicanard.globalmarketchest.database.querybuilder.QueryBuilder;
import fr.epicanard.globalmarketchest.exceptions.ShopAlreadyExistException;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;

public class ShopManager {
  private List<ShopInfo> shops = new ArrayList<ShopInfo>();

  /**
   * Remove every metadata from shop and clear the shops list
   */
  public void resetShopList() {
    this.shops.forEach(shop -> shop.removeMetadata());
    this.shops.clear();
  }

  /**
   * Update shops informations from Database informations
   */
  public void updateShops() {
    this.resetShopList();

    QueryBuilder builder = new QueryBuilder(DatabaseConnection.tableShops);
    builder.execute(builder.select(), res -> {
      try {
        while (res.next()) {
          ShopInfo shop = new ShopInfo(res);
          shop.getSignLocation().getBlock().setMetadata(ShopUtils.META_KEY, new FixedMetadataValue(GlobalMarketChest.plugin, shop));
          Optional.ofNullable(shop.getOtherLocation()).ifPresent(loc -> loc.getBlock().setMetadata(ShopUtils.META_KEY, new FixedMetadataValue(GlobalMarketChest.plugin, shop)));
          this.shops.add(shop);
        }
      } catch(SQLException e) {e.printStackTrace();}
    });
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

    AtomicInteger id = new AtomicInteger(-1);
    Consumer<ResultSet> cs = res -> {
      id.set(DatabaseUtils.getId(res));
    };
    if (builder.execute(builder.insert(), cs))
      this.updateShops();
    return id.get();
  }


  /**
   * Delete shop at the specific location
   */
  public Boolean deleteShop(ShopInfo shop) {
    if (shop == null)
      return false;

    this.shops.removeIf(s -> s.getId() == shop.getId());
    shop.removeMetadata();

    QueryBuilder builder = new QueryBuilder(DatabaseConnection.tableShops);

    builder.addCondition("id", shop.getId());
    return builder.execute(builder.delete());
  }
}
