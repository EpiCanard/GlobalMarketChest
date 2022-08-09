package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.ShopAlreadyExistException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.ChatInput;
import fr.epicanard.globalmarketchest.gui.actions.LeaveShop;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.paginator.Paginator;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.ShopCreationInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.shops.ShopType;
import fr.epicanard.globalmarketchest.utils.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

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
    this.actions.put(40, new ChatInput("InfoMessages.WriteGroupName", this::changeName));
    this.actions.put(53, this::createShop);
  }

  /**
   * Create the shop inside the database and leave the GUI
   * Drop the sign if the shop already exist
   *
   * @param gui InventoryGUI used shop creation
   */
  private void createShop(InventoryGUI gui) {
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);
    try {
      GlobalMarketChest.plugin.shopManager.createShop(shop);

      Utils.editSign(this.inv.getTransactionValue(TransactionKey.SIGN_LOCATION), new String[]{
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
    final List<ShopInfo> lst = pag.getSubList(GlobalMarketChest.plugin.shopManager.getShops());
    final List<ItemStack> items = pag.getItemstacks();
    items.clear();

    for (ShopInfo shop : lst) {
      ItemStack item = ItemStackUtils.getItemStack("minecraft:ender_chest");
      final List<String> lore = ShopUtils.generateLore(shop);

      lore.addAll(Utils.toList(LangUtils.get("Shops.ClickChangeGroup")));
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
    final List<ShopInfo> subShops = this.paginator.getSubList(GlobalMarketChest.plugin.shopManager.getShops());

    if (pos < subShops.size()) {
      this.changeName(subShops.get(pos).getGroup());
    }
  }

  /**
   * Change the groupe name of the shop
   *
   * @param name Name of the group
   */
  public void changeName(String name) {
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);

    if (shop != null)
      shop.setGroup(name);
    this.updateName();
  }
}
