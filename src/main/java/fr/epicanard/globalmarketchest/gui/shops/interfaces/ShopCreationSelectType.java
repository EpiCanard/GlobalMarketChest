package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.paginator.Paginator;
import fr.epicanard.globalmarketchest.gui.shops.ShopCreationInterface;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopType;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.ItemUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;

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
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);
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
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);

    List<Block> blocks = Utils.filter(WorldUtils.getNearAllowedBlocks(shop.getSignLocation()), block -> ShopUtils.getShop(block) == null);
    List<ItemStack> items = pag.getSubList(blocks.stream().map(block -> new ItemStack(block.getType())).collect(Collectors.toList()));
    pag.getItemstacks().clear();
    pag.getItemstacks().addAll(items);
  }

  /**
   * Get the block at the position and set as the otherLocation inside the shop
   *
   * @param pos Position inside the inventory
   */
  private void setOtherLocation(int pos) {
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);
    List<Block> blocks = this.paginator.getSubList(WorldUtils.getNearAllowedBlocks(shop.getSignLocation()));

    try {
      Block block = blocks.get(pos);

      shop.setOtherLocation(block.getLocation());
      this.updateName();
    } catch(IndexOutOfBoundsException e) {}
  }

  /**
   * Verify if the shop type is set if not display warning
   *
   * @return if there is an error return false else true
   */
  private Boolean checkCreation() {
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);

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

    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);
    this.setGlow(13, shop.getType(), ShopType.GLOBALSHOP);
    // this.setGlow(11, shop.getType(), ShopType.GLOBALSHOP);
    // this.setGlow(13, shop.getType(), ShopType.AUCTIONSHOP);
    // this.setGlow(15, shop.getType(), ShopType.ADMINSHOP);
  }
}
