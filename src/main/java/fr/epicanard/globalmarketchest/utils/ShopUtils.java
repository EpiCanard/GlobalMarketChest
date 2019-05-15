package fr.epicanard.globalmarketchest.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.MissingMethodException;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopType;
import fr.epicanard.globalmarketchest.utils.annotations.AnnotationCaller;
import fr.epicanard.globalmarketchest.utils.annotations.Version;
import lombok.experimental.UtilityClass;

/**
 * Utility Class about Shops
 */
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

  /**
   * Generate a custom random name
   *
   * @return
   */
  public String generateName() {
    return "Shop_" + RandomStringUtils.randomAlphabetic(5);
  }

  /**
   * Generate a name for localshop
   *
   * @return
   */
  public String generateLocalName(int id) {
    return "LocalShop" + id;
  }

  /**
   * Get the shop at the block position
   *
   * @param bl Block linked to a shop
   * @return Return the ShopInfo or null
   */
  public ShopInfo getShop(Block bl) {
    try {
      return (ShopInfo)bl.getMetadata(ShopUtils.META_KEY).get(0).value();
    } catch(IndexOutOfBoundsException | NullPointerException e) {
      return null;
    }
  }

  /**
   * Generate a String key/value
   *
   * @param key
   * @param value
   */
  public String generateKeyValue(String key, String value) {
    return "&2" + key + " : &9" + value;
  }

  /**
   * Generate a string with the initial of shop Type
   *
   * @param shop Shop used
   * @return Return string generated
   */
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

  /**
   * Generate Lore for specific shop
   *
   * @return Return lore as String array
   */
  public List<String> generateLore(ShopInfo shop) {
    List<String> lore = new ArrayList<>();

    lore.add(ShopUtils.generateKeyValue(LangUtils.get("Divers.Location"), WorldUtils.getStringFromLocation(shop.getSignLocation())));
    lore.add(ShopUtils.generateKeyValue(LangUtils.get("Divers.Group"), shop.getGroup()));
    lore.add(ShopUtils.generateKeyValue(LangUtils.get("Divers.Type"), ShopUtils.generateShopType(shop)));
    return lore;
  }

  /**
   * Generate Lore for specific shop with otherLocation
   *
   * @return Return lore as String array
   */
  public List<String> generateLoreWithOther(ShopInfo shop) {
    List<String> lore = new ArrayList<>();

    lore.add(ShopUtils.generateKeyValue(LangUtils.get("Divers.Location"), WorldUtils.getStringFromLocation(shop.getSignLocation())));
    lore.add(ShopUtils.generateKeyValue(LangUtils.get("Divers.OtherLocation"), WorldUtils.getStringFromLocation(shop.getOtherLocation())));
    lore.add(ShopUtils.generateKeyValue(LangUtils.get("Divers.Group"), shop.getGroup()));
    lore.add(ShopUtils.generateKeyValue(LangUtils.get("Divers.Type"), ShopUtils.generateShopType(shop)));
    return lore;
  }

  /**
   * Get Allowed Block that can be linked with a shop
   *
   * @return Return the list of allowed Material
   */
  public List<Material> getAllowedLinkBlock() {
    return ShopUtils.allowedBlock;
  }

  /**
   * Get a list of materials for sign (sign and wall sign) for 1.13 version
   * 
   * @return Return a set of material string name
   */
  @Version(name="getMaterialSigns", versions={"1.13"})
  public Set<String> getMaterialSigns_1_13() {
    return new HashSet<>(Arrays.asList(
      "SIGN",
      "WALL_SIGN"
    ));
  }

  /**
   * Get a list of materials for sign (sign and wall sign) for 1.14 and higher version
   * 
   * @return Return a set of material string name
   */
  @Version(name="getMaterialSigns")
  public Set<String> getMaterialSigns_latest() {
    return new HashSet<>(Arrays.asList(
      "OAK_SIGN",
      "OAK_WALL_SIGN",
      "SPRUCE_SIGN",
      "SPRUCE_WALL_SIGN",
      "BIRCH_SIGN",
      "BIRCH_WALL_SIGN",
      "JUNGLE_SIGN",
      "JUNGLE_WALL_SIGN",
      "ACACIA_SIGN",
      "ACACIA_WALL_SIGN",
      "DARK_OAK_SIGN",
      "DARK_OAK_WALL_SIGN"
    ));
  }

  /**
   * Define if the block is a sign
   * 
   * @param block The block
   * @return
   */
  public boolean isSign(Material material) {
    try {
      final Set<String> signs = AnnotationCaller.call("getMaterialSigns", ShopUtils.class, null, (Object[])null);

      return signs.stream().map((name) -> {
        return Material.getMaterial(name);
      }).filter((mat) -> {
        return mat != null && mat.equals(material);
      }).findFirst().isPresent();
    } catch (MissingMethodException e) {
      e.printStackTrace();
    }
    return false;
  }
}
