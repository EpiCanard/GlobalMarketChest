package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.paginator.Paginator;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.ShopInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ShopCreationAttachBlock extends ShopInterface {

  public ShopCreationAttachBlock(InventoryGUI inv) {
    super(inv);

    this.actions.put(0, new PreviousInterface());
    this.paginator.setLoadConsumer(this::loadNearBlock);
    this.paginator.setClickConsumer(this::setOtherLocation);
  }

  /**
   * Get near allowed block around the sign and add it in paginator
   *
   * @param pag Paginator used
   */
  private void loadNearBlock(Paginator pag) {
    final Location searchLocation = this.inv.getTransactionValue(TransactionKey.SIGN_LOCATION);

    final List<Block> blocks = this.getNearMatchingBlocks(searchLocation);
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
    final Location searchLocation = this.inv.getTransactionValue(TransactionKey.SIGN_LOCATION);
    final List<Block> blocks = this.paginator.getSubList(getNearMatchingBlocks(searchLocation));

    try {
      final Block block = blocks.get(pos);

      shop.setOtherLocation(Optional.of(block.getLocation()));
      this.inv.unloadLastInterface();
    } catch (IndexOutOfBoundsException e) {
      return;
    }
  }

  private List<Block> getNearMatchingBlocks(Location loc) {
    return Utils.filter(WorldUtils.getNearAllowedBlocks(loc), block -> ShopUtils.getShop(block) == null);
  }
}
