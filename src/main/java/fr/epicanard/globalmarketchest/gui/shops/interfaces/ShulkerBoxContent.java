package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.exceptions.InterfaceLoadException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.DefaultFooter;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class ShulkerBoxContent extends DefaultFooter {

  public ShulkerBoxContent(final InventoryGUI inv) {
    super(inv);
    this.actions.put(0, new PreviousInterface());
  }

  @Override
  public void load() {
    super.load();
    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTION_INFO);
    final ItemStack item = DatabaseUtils.deserialize(auction.getItemMeta());

    final ItemStack[] items = this.getShulkerBoxContent(item);

    if (items != null) {
      for (int i = 0; i < items.length; i++) {
        this.inv.getInv().setItem(i + 18, items[i]);
      }
    } else {
      throw new InterfaceLoadException(String.format(
          "The auction '%d : %s' is not a shulker, it is impossible to see its content",
          auction.getId(), auction.getItemStack()
      ));
    }
    final AuctionLoreConfig loreConfig = this.inv.getTransactionValue(TransactionKey.AUCTION_LORE_CONFIG);
    this.setIcon(ItemStackUtils.addItemStackLore(item, auction.getLore(loreConfig)));
  }

  /**
   * Get shulker box content to display theme
   *
   * @param item Shulker box item
   * @return Shulker box content
   */
  private ItemStack[] getShulkerBoxContent(final ItemStack item) {
    if(item.getItemMeta() instanceof BlockStateMeta){
      final BlockStateMeta itemMeta = (BlockStateMeta)item.getItemMeta();
      if(itemMeta.getBlockState() instanceof ShulkerBox){
        final ShulkerBox shulker = (ShulkerBox) itemMeta.getBlockState();
        return shulker.getInventory().getContents();
      }
    }
    return null;
  }

  /**
   * Check if item sent in parameter is a shulker box
   *
   * @param item Item to verify if it is a shulker
   * @return if it is a shulker
   */
  public static boolean isShulker(final ItemStack item) {
    if (item != null && item.getItemMeta() instanceof BlockStateMeta) {
      final BlockStateMeta itemMeta = (BlockStateMeta) item.getItemMeta();
      return itemMeta.getBlockState() instanceof ShulkerBox;
    }
    return false;
  }
}
