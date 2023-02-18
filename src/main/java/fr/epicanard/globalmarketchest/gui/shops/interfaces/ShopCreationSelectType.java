package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.gui.paginator.Paginator;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.ShopCreationInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopType;
import fr.epicanard.globalmarketchest.utils.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Shop Interface for Creation Process
 * Step 1 : Selection shop type and Choose to link with another block (chest, etc)
 */
public class ShopCreationSelectType extends ShopCreationInterface {

  public ShopCreationSelectType(InventoryGUI inv) {
    super(inv);
    // this.actions.put(11, i -> this.toggleShop(11, ShopType.GLOBALSHOP));
    // this.actions.put(13, i -> this.toggleShop(13, ShopType.AUCTIONSHOP));
    // this.actions.put(15, i -> this.toggleShop(15, ShopType.ADMINSHOP));
    this.actions.put(53, new NextInterface("ShopCreationLink", this::checkCreation));
    if (this.paginator != null) {
      this.paginator.setLoadConsumer(this::loadNearBlock);
      this.paginator.setClickConsumer(this::setOtherLocation);
    }
  }

  /**
   * Set or unset glow on item below specific position (if type is set on mask or not)
   *
   * @param pos   Position of the item
   * @param mask  shop mask
   * @param type  Type of shop
   */
  private void setGlow(int pos, int mask, ShopType type) {
    ItemUtils.setGlow(this.inv.getInv(), pos + 9, type.isSetOn(mask));
  }

  /**
   * Toggle the ShopType in the specific shop
   *
   * @param pos   Position in the inventory of the type
   * @param type  Type to toggle
   */
  private void toggleShop(int pos, ShopType type) {
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    shop.toggleType(type);

    this.setGlow(pos, shop.getType(), type);
    this.updateName();
    this.inv.getWarn().stopWarn();
  }

  /**
   * Get near allowed block around the sign and add it in paginator
   *
   * @param pag Paginator used
   */
  private void loadNearBlock(Paginator pag) {
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);

    final List<Block> blocks = shop.getSignLocation().map(this::getNearMatchingBlocks).orElse(List.of());
    final List<ItemStack> items = pag.getSubList(blocks.stream().map(block -> {
      final ItemStack item = new ItemStack(block.getType());
      ItemStackUtils.addItemStackLore(
          item,
          Arrays.asList(ShopUtils.generateKeyValue(LangUtils.get("Divers.OtherLocation"), WorldUtils.getStringFromLocation(block.getLocation())))
      );
      return item;
    }).collect(Collectors.toList()));
    pag.getItemstacks().clear();
    pag.getItemstacks().addAll(items);
  }

  /**
   * Get the block at the position and set as the otherLocation inside the shop
   *
   * @param pos Position inside the inventory
   */
  private void setOtherLocation(int pos) {
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    final List<Block> blocks = shop.getSignLocation().map(loc -> this.paginator.getSubList(getNearMatchingBlocks(loc))).orElse(List.of());

    try {
      final Block block = blocks.get(pos);

      shop.setOtherLocation(Optional.of(block.getLocation()));
      this.updateName();
    } catch (IndexOutOfBoundsException e) {
      return;
    }
  }

  private List<Block> getNearMatchingBlocks(Location loc) {
    return Utils.filter(WorldUtils.getNearAllowedBlocks(loc), block -> ShopUtils.getShop(block) == null);
  }

  /**
   * Verify if the shop type is set if not display warning
   *
   * @return if there is an error return false else true
   */
  private Boolean checkCreation() {
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);

    if (shop != null && shop.getType() > 0) {
      this.inv.getWarn().stopWarn();
      return true;
    }
    this.inv.getWarn().warn("ShopTypeNotSet", 40);
    return false;
  }

  /**
   * Called when loading the interface
   */
  @Override
  public void load() {
    super.load();

    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    this.setGlow(13, shop.getType(), ShopType.GLOBALSHOP);
    // this.setGlow(11, shop.getType(), ShopType.GLOBALSHOP);
    // this.setGlow(13, shop.getType(), ShopType.AUCTIONSHOP);
    // this.setGlow(15, shop.getType(), ShopType.ADMINSHOP);
  }
}
