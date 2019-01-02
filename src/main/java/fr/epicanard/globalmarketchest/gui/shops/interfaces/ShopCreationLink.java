package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.ShopAlreadyExistException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.paginator.Paginator;
import fr.epicanard.globalmarketchest.gui.shops.ShopCreationInterface;
import fr.epicanard.globalmarketchest.gui.actions.LeaveShop;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopType;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

/**
 * Shop Interface for Creation Process
 * Step 2 : Set the groupe name of the shop, can link the group name wither another shop
 */
public class ShopCreationLink extends ShopCreationInterface {

  public ShopCreationLink(InventoryGUI inv) {
    super(inv);
    if (this.paginator != null) {
      this.paginator.setLoadConsumer(this::loadZone);
      this.paginator.setClickConsumer(this::changeName);
    }
    this.actions.put(0, new PreviousInterface());
    this.actions.put(53, this::createShop);
  }

  /**
   * Create the shop inside the database and leave the GUI
   * Drop the sign if the shop already exist
   *
   * @param gui InventoryGUI used shop creation
   */
  private void createShop(InventoryGUI gui) {
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);
    try {
      GlobalMarketChest.plugin.shopManager.createShop(shop);

      Utils.editSign(this.inv.getTransactionValue(TransactionKey.SIGNLOCATION), new String[] {
        ShopType.GLOBALSHOP.getDisplayName()
      });

    } catch (ShopAlreadyExistException e) {
      PlayerUtils.sendMessage(gui.getPlayer(), e.getMessage());
      shop.getSignLocation().getBlock().breakNaturally();
    } finally {
      Consumer<InventoryGUI> exit = new LeaveShop();
      exit.accept(gui);
    }
  }

  /**
   * Load the shop link zone
   *
   * @param pag Paginator used
   */
  public void loadZone(Paginator pag) {
    List<ShopInfo> lst = pag.getSubList(GlobalMarketChest.plugin.shopManager.getShops());
    List<ItemStack> items = pag.getItemstacks();
    String clickInfo = "&c" + LangUtils.get("Shops.ClickChangeGroup");
    items.clear();

    for (int i = 0; i < lst.size(); i++) {
      ItemStack item = ItemStackUtils.getItemStack("minecraft:ender_chest");
      ShopInfo shop = lst.get(i);
      List<String> lore = ShopUtils.generateLore(shop);

      lore.set(3, clickInfo);
      item = ItemStackUtils.setItemStackMeta(item, "Shop", lore);
      items.add(item);
    }
  }

  /**
   * Get the group name of the shop at position inside the inventory and set on current shop
   *
   * @param pos Position inside the inventory
   */
  public void changeName(Integer pos) {
    List<ShopInfo> subShops = this.paginator.getSubList(GlobalMarketChest.plugin.shopManager.getShops());
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);

    if (shop != null)
      shop.setGroup(subShops.get(pos).getGroup());
    this.updateName();
  }
}
