package fr.epicanard.globalmarketchest.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class ShopUtils {
  public static String generateName() {
    return "Shop_" + RandomStringUtils.randomAlphabetic(5);
  }

  public static String generateLocalName(int id) {
    return "LocalShop" + id;
  }
}
