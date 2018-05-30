package fr.epicanard.globalmarketchest.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ShopUtils {
  public final String META_KEY = "GMC_SHOP";
  private List<Material> allowedBlock = new ArrayList<Material>();

  public void init()  {
    List<String> blocks = GlobalMarketChest.plugin.getConfigLoader().getConfig().getStringList("AllowedLinkBlock");

    ShopUtils.allowedBlock.clear();
    blocks.forEach(bl -> {
      ItemStack item = ItemStackUtils.getItemStack(bl);
      if (item != null)
        ShopUtils.allowedBlock.add(item.getType());
    });
  }

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

  public String generateKeyValue(String key, String value) {
    return "&2" + key + " : &9" + value;
  }

  private String generateShopType(ShopInfo shop) {
    String ret = "";

    if (ShopType.GLOBALSHOP.isSetOn(shop.getType()))
      ret += "&9G";
    if (ShopType.AUCTIONSHOP.isSetOn(shop.getType()))
      ret += "&bA";
    if (ShopType.ADMINSHOP.isSetOn(shop.getType()))
      ret += "&cD";
    return ret;
  }

  public String[] generateLore(ShopInfo shop) {
    String[] lore = {
      ShopUtils.generateKeyValue(LangUtils.get("Divers.Location"), WorldUtils.getStringFromLocation(shop.getSignLocation())),
      ShopUtils.generateKeyValue(LangUtils.get("Divers.Group"), shop.getGroup()),
      ShopUtils.generateKeyValue(LangUtils.get("Divers.Type"), ShopUtils.generateShopType(shop)),
      ""
    };
    return lore;
  }

  public String[] generateLoreWithOther(ShopInfo shop) {
    String[] lore = {
      ShopUtils.generateKeyValue(LangUtils.get("Divers.Location"), WorldUtils.getStringFromLocation(shop.getSignLocation())),
      ShopUtils.generateKeyValue(LangUtils.get("Divers.OtherLocation"), WorldUtils.getStringFromLocation(shop.getOtherLocation())),
      ShopUtils.generateKeyValue(LangUtils.get("Divers.Group"), shop.getGroup()),
      ShopUtils.generateKeyValue(LangUtils.get("Divers.Type"), ShopUtils.generateShopType(shop)),
    };
    return lore;
  }

  public List<Material> getAllowedLinkBlock() {
    return ShopUtils.allowedBlock;
  }

}
