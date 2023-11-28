package fr.epicanard.globalmarketchest.shops;


import fr.epicanard.globalmarketchest.utils.ConfigUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import lombok.Getter;

/**
 * Enum for define the shop type
 */
public enum ShopType {
  GLOBALSHOP(1, "GlobalShop"),
  ADMINSHOP(2, "AdminShop");
  // AUCTIONSHOP(3, "AuctionShop"),
  // LOCALSHOP(4, "LocalShop")

  @Getter
  private int shopId;
  private String confName;

  ShopType(int id, String confName) {
    this.shopId = id;
    this.confName = confName;
  }

  /**
   * Get the first line to use to create shop
   *
   * @return
   */
  public String getFirstLineToCreate() {
    return ConfigUtils.getString(
      String.format("Sign.Appearance.%s.FirstLineToCreate", this.confName));
  }

  /**
   * Get the display name of the shop
   *
   * @return
   */
  public String getDisplayName() {
    return Utils.toColor(ConfigUtils.getString(
      String.format("Sign.Appearance.%s.DisplayName", this.confName)));
  }

  /**
   * Get error display name of the shop
   *
   * @return
   */
  public String getErrorDisplayName() {
    return Utils.toColor(ConfigUtils.getString("Sign.Appearance.NotWorkingShopDisplayName"));
  }

  public static ShopType fromId(int id) {
    for (ShopType type : ShopType.values()) {
      if (type.shopId == id)
        return type;
    }
    return ShopType.GLOBALSHOP;
  }
}
