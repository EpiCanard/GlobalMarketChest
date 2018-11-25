package fr.epicanard.globalmarketchest.shops;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.Utils;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Enum for define the shop type
 */
public enum ShopType {
  GLOBALSHOP(1, "GlobalShop"),
  AUCTIONSHOP(2, "AuctionShop"),
  ADMINSHOP(4, "AdminShop"),
  LOCALSHOP(8, "LocalShop")
  ;

  private int shopMask;
  private String confName;

  ShopType(int id, String confName) {
    this.shopMask = id;
    this.confName = confName;
  }

  /**
   * Define if this ShopType is set on this mask
   *
   * @param types the mask
   * @return
   */
  public Boolean isSetOn(int types) {
    return ((types & this.shopMask) == this.shopMask);
  }

  /**
   * Add this ShopType to the mask
   *
   * @param types the mask
   * @return the mask
   */
  public int setOn(int types) {
    return types | this.shopMask;
  }

  /**
   * Remove this ShopType from the mask
   *
   * @param type the mask
   * @return the mask
   */
  public int unsetOn(int types) {
    return types & ~this.shopMask;
  }

  /**
   * Toggle this ShopType from the mask
   *
   * @param type the mask
   * @return the mask
   */
  public int toggle(int types) {
    return (this.isSetOn(types)) ? this.unsetOn(types) : this.setOn(types);
  }

  /**
   * Get the first line to use to create shop
   *
   * @return
   */
  public String getFirstLineToCreate() {
    return GlobalMarketChest.plugin.getConfigLoader().getConfig().getString(
      String.format("Sign.Appearance.%s.FirstLineToCreate", this.confName));
  }

  /**
   * Get the display name of the shop
   *
   * @return
   */
  public String getDisplayName() {
    return Utils.toColor(GlobalMarketChest.plugin.getConfigLoader().getConfig().getString(
      String.format("Sign.Appearance.%s.DisplayName", this.confName)));
  }

  /**
   * Get error display name of the shop
   *
   * @return
   */
  public String getErrorDisplayName() {
    return Utils.toColor(GlobalMarketChest.plugin.getConfigLoader().getConfig()
      .getString("Sign.Appearance.NotWorkingShopDisplayName"));
  }
}
