package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.ShopAlreadyExistException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.paginator.Paginator;
import fr.epicanard.globalmarketchest.gui.shops.ShopCreationInterface;
import fr.epicanard.globalmarketchest.gui.actions.LeaveShop;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;

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

  private void createShop(InventoryGUI i) {
    ShopInfo shop = this.inv.getTransValue("ShopInfo");
    try {
      GlobalMarketChest.plugin.shopManager.createShop(shop);
      Consumer<InventoryGUI> exit = new LeaveShop();
      exit.accept(i);
    } catch (ShopAlreadyExistException e) {
      PlayerUtils.sendMessagePlayer(i.getPlayer(), e.getMessage());
      shop.getSignLocation().getBlock().breakNaturally();
      Consumer<InventoryGUI> exit = new LeaveShop();
      exit.accept(i);
    }
  }

  public void loadZone(Paginator pag) {
    List<ShopInfo> lst = pag.getSubList(GlobalMarketChest.plugin.shopManager.getShops());
    List<ItemStack> items = pag.getItemstacks();
    String clickInfo = "&c" + LangUtils.get("Shops.ClickChangeGroup");
    items.clear();

    for (int i = 0; i < lst.size(); i++) {
      ItemStack item = ItemStackUtils.getItemStack("minecraft:ender_chest");
      ShopInfo shop = lst.get(i);
      String[] lore = ShopUtils.generateLore(shop);

      lore[3] = clickInfo;
      item = ItemStackUtils.setItemStackMeta(item, "Shop", lore);
      items.add(item);
    }
  }

  public void changeName(Integer pos) {
    List<ShopInfo> subShops = this.paginator.getSubList(GlobalMarketChest.plugin.shopManager.getShops());
    ShopInfo shop = this.inv.getTransValue("ShopInfo");

    if (shop != null)
      shop.setGroup(subShops.get(pos).getGroup());
    this.updateName();
  }

  @Override
  public void load() {
    super.load();
  }

  @Override
  public void unload() {
  }
}
