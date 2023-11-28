package fr.epicanard.globalmarketchest.managers;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.database.DatabaseManager;
import fr.epicanard.globalmarketchest.database.connectors.DatabaseConnector;
import fr.epicanard.globalmarketchest.database.querybuilder.QueryExecutor;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.DeleteBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.InsertBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.SelectBuilder;
import fr.epicanard.globalmarketchest.database.querybuilder.builders.UpdateBuilder;
import fr.epicanard.globalmarketchest.exceptions.ShopAlreadyExistException;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.WorldUtils;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fr.epicanard.globalmarketchest.utils.Option.exists;

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
   * Load the list of shops
   */
  public void loadShops() {
    this.updateShops();
    this.shops = this.shops.stream().map(shop -> {
      if (shop.getExists() && !shop.getTpLocation().isPresent() && shop.getSignLocation().isPresent()) {
        shop.getSignLocation().ifPresent(loc -> {
          Location tp = loc.clone().add(0.5, 0, 0.5);
          updateTpLocation(shop.getId(), tp);
          shop.setTpLocation(Optional.of(tp));
        });
      }
      return shop;
    }).collect(Collectors.toList());
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

  /**
   * Create a shop inside database and add it in list shops
   *
   * @param shop Info about a shop
   */
  public void createShop(ShopInfo shop) throws ShopAlreadyExistException {
    Boolean shopAlreadyExists = checkAlreadyExist(shop.getSignLocation(), true) || checkAlreadyExist(shop.getOtherLocation(), false);

    if (shopAlreadyExists)
      throw new ShopAlreadyExistException(shop.getSignLocation().get());

    final InsertBuilder builder = insert()
        .addValue("owner", shop.getOwner())
        .addValue("signLocation", shop.getSignLocation().map(WorldUtils::getStringFromLocation))
        .addValue("otherLocation", shop.getOtherLocation().map(WorldUtils::getStringFromLocation))
        .addValue("tpLocation", shop.getTpLocation().map(WorldUtils::getStringFromLocation))
        .addValue("type", shop.getType().getShopId())
        .addValue("group", shop.getGroup())
        .addValue("server", shop.getServer());

    if (QueryExecutor.of().execute(builder))
      this.updateShops();
  }

  /**
   * Set the teleport location of a shop
   *
   * @param shop Shop to update
   * @param newLocation New teleport location to save in db
   */
  public void setTpLocation(ShopInfo shop, Location newLocation) {
    updateTpLocation(shop.getId(), newLocation);
    shop.setTpLocation(Optional.ofNullable(newLocation));
  }

  /**
   * Check if the location exists in the shop list
   */
  private Boolean checkAlreadyExist(Optional<Location> location, Boolean sign) {
    return exists(location, loc -> this.shops.stream().anyMatch(shop ->
        exists((sign) ? shop.getSignLocation() : shop.getOtherLocation(), shopLoc -> WorldUtils.compareLocations(shopLoc, loc))
      ));
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
  private void updateShops() {
    this.resetShopList();

    final SelectBuilder builder = select();
    QueryExecutor.of().execute(builder, res -> {
      while (res.next()) {
        final ShopInfo shop = new ShopInfo(res);
        if (exists(shop.getLocation(), sign -> sign.getWorld() != null) && shop.getServer().equals(GlobalMarketChest.plugin.getServerName())) {
          shop.addMetadata();
        } else {
          shop.setExists(false);
        }
        this.shops.add(shop);
      }
    }, Exception::printStackTrace);
  }

  /**
   * Update the value of tpLocation for a specific shop
   */
  private void updateTpLocation(int id, Location tpLocation) {
    final UpdateBuilder builder = update()
      .addValue("tpLocation", WorldUtils.getStringFromLocation(tpLocation))
      .addCondition("id", id);
    QueryExecutor.of().execute(builder, null, Exception::printStackTrace);
  }
}
