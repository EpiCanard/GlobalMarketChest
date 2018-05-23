package fr.epicanard.globalmarketchest.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.block.Block;

import fr.epicanard.globalmarketchest.shops.ShopInfo;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ShopUtils {
  public final String META_KEY = "GMC_SHOP"; 

  public String generateName() {
    return "Shop_" + RandomStringUtils.randomAlphabetic(5);
  }

  public String generateLocalName(int id) {
    return "LocalShop" + id;
  }

  public ShopInfo getShop(Block bl) {
    try {
      return (ShopInfo)bl.getMetadata(ShopUtils.META_KEY).get(0).value();
    } catch(IndexOutOfBoundsException | NullPointerException e) {
      return null;
    }
  }
}
