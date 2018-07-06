package fr.epicanard.globalmarketchest.gui.shops;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.economy.VaultEconomy;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.actions.NewAuction;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class DefaultFooter extends ShopInterface {
  public DefaultFooter(InventoryGUI inv) {
    super(inv);
    this.actions.put(53, new NewAuction());
    this.actions.put(46, new NextInterface("AuctionGlobalView"));
  }

  @Override
  public void load() {
    super.load();
    ItemStack item = this.inv.getInv().getItem(45);
    VaultEconomy eco = GlobalMarketChest.plugin.economy;

    ItemStackUtils.setItemStackLore(item, 
      Utils.toList(String.format("&3%s",
      eco.getEconomy().format(eco.getMoneyOfPlayer(this.inv.getPlayer().getUniqueId()))
    )));
    this.inv.getInv().setItem(45, item);
  }
}
