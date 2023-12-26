package fr.epicanard.globalmarketchest.utils;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopType;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Utility Class about Shops
 */
@UtilityClass
public class ShopUtils {
  public final String META_KEY = "GMC_SHOP";
  private List<Material> allowedBlock = new ArrayList<Material>();
  private List<String> lockedShop = new ArrayList<String>();

  private Set<String> SIGN_MATERIALS;

  static {
    switch (Utils.getVersion()) {
      case "1.12" :
        SIGN_MATERIALS = new HashSet<>(Arrays.asList(
          "SIGN_POST",
          "WALL_SIGN"
        ));
        break;
      case "1.13" :
        SIGN_MATERIALS = new HashSet<>(Arrays.asList(
          "SIGN",
          "WALL_SIGN"
        ));
        break;
      default:
        SIGN_MATERIALS =  Arrays.stream(Material.values())
          .filter(m -> m.name().endsWith("_SIGN") && !m.name().contains("LEGACY"))
          .map(Material::name)
          .collect(Collectors.toSet());
    }
  }

  public void init() {
    List<String> blocks = ConfigUtils.getStringList("AllowedLinkBlock");

    ShopUtils.allowedBlock.clear();
    blocks.forEach(bl -> {
      ItemStack item = ItemStackUtils.getItemStack(bl);
      if (item != null)
        ShopUtils.allowedBlock.add(item.getType());
    });
    ShopUtils.lockedShop.clear();
  }

  /**
   * Generate a custom random name
   *
   * @return Generated shop name
   */
  public String generateName() {
    return "Shop_" + RandomStringUtils.randomAlphabetic(5);
  }

  /**
   * Generate a name for localshop
   *
   * @return Local shop name
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
      return (ShopInfo) bl.getMetadata(ShopUtils.META_KEY).get(0).value();
    } catch (IndexOutOfBoundsException | NullPointerException e) {
      return null;
    }
  }

  /**
   * Generate a String key/value
   *
   * @param key   Key
   * @param value Value
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

    if (ShopType.GLOBALSHOP.equals(shop.getType()))
      ret += "&9GlobalShop";
    if (ShopType.ADMINSHOP.equals(shop.getType()))
      ret += "&cAdminShop";
    return ret;
  }

  /**
   * Generate Lore for specific shop
   *
   * @return Return lore as String array
   */
  public List<String> generateLore(ShopInfo shop) {
    final List<String> lore = new ArrayList<>();

    shop.getSignLocation().map(WorldUtils::getStringFromLocation)
        .ifPresent(loc -> lore.add(ShopUtils.generateKeyValue(LangUtils.get("Divers.Location"), loc)));
    lore.add(ShopUtils.generateKeyValue(LangUtils.get("Divers.Server"), shop.getServer()));
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
    final List<String> lore = new ArrayList<>();

    shop.getSignLocation().map(WorldUtils::getStringFromLocation)
        .ifPresent(loc -> lore.add(ShopUtils.generateKeyValue(LangUtils.get("Divers.Location"), loc)));
    shop.getOtherLocation().map(WorldUtils::getStringFromLocation)
        .ifPresent(loc -> lore.add(ShopUtils.generateKeyValue(LangUtils.get("Divers.OtherLocation"), loc)));
    lore.add(ShopUtils.generateKeyValue(LangUtils.get("Divers.Server"), shop.getServer()));
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
   * Define if the block is a sign
   *
   * @param material Block material
   * @return If it is a sign
   */
  public boolean isSign(Material material) {
    return SIGN_MATERIALS.contains(material.name());
  }

  /**
   * Define if a shop is locked
   *
   * @param shopGroup String shop group name
   * @return if current shop is locked
   */
  public boolean isLockedShop(String shopGroup) {
    return lockedShop.contains(shopGroup);
  }

  /**
   * Lock a shop group
   *
   * @param shopGroup String shop group name
   */
  public void lockShop(String shopGroup) {
    lockedShop.add(shopGroup);
    GlobalMarketChest.plugin.inventories.getInventories().forEach((key, value) -> {
      ShopInfo shop = value.getTransactionValue(TransactionKey.SHOP_INFO);
      if (shop != null && shop.getGroup().equals(shopGroup)) {
        Bukkit.getScheduler().runTask(GlobalMarketChest.plugin,
            () -> GlobalMarketChest.plugin.inventories.removeInventory(key));
        PlayerUtils.sendMessageConfig(Bukkit.getServer().getPlayer(key), "InfoMessages.ShopTemporarilyLocked");
      }
    });
  }

  /**
   * Unlock a shop group
   *
   * @param shopGroup String shop group name
   */
  public void unlockShop(String shopGroup) {
    lockedShop.removeIf(shop -> shop == shopGroup);
  }

  /**
   * Open a globalshop for a player
   *
   * @param player  Player on which open the shop
   * @param shop    Name of the shop to open
   * @param success Success callback
   */
  public void openShop(Player player, ShopInfo shop, Consumer<InventoryGUI> success) {
    if (ShopUtils.isLockedShop(shop.getGroup())) {
      PlayerUtils.sendMessageConfig(player, "InfoMessages.ShopTemporarilyLocked");
      return;
    }

    if (GlobalMarketChest.plugin.inventories.hasInventory(player.getUniqueId())) {
      if (GlobalMarketChest.plugin.inventories.getInventory(player.getUniqueId()).getChatEditing())
        return;
      GlobalMarketChest.plugin.inventories.removeInventory(player.getUniqueId());
    }
    player.closeInventory();
    final InventoryGUI inv = new InventoryGUI(player);
    GlobalMarketChest.plugin.inventories.addInventory(player.getUniqueId(), inv);
    inv.getTransaction().put(TransactionKey.SHOP_INFO, shop);
    inv.open();
    success.accept(inv);
  }
}
