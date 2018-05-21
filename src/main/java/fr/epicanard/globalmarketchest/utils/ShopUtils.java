package fr.epicanard.globalmarketchest.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.block.Block;

import fr.epicanard.globalmarketchest.shops.ShopInfo;

public class ShopUtils {
  public static final String META_KEY = "GMC_SHOP"; 

  public static String generateName() {
    return "Shop_" + RandomStringUtils.randomAlphabetic(5);
  }

  public static String generateLocalName(int id) {
    return "LocalShop" + id;
  }

  public static ShopInfo getShop(Block bl) {
    try {
      return (ShopInfo)bl.getMetadata(ShopUtils.META_KEY).get(0).value();
    } catch(IndexOutOfBoundsException | NullPointerException e) {
      return null;
    }
  }
}
