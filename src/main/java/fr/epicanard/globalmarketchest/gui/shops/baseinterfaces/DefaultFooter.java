package fr.epicanard.globalmarketchest.gui.shops.baseinterfaces;

import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.economy.VaultEconomy;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.NewAuction;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class DefaultFooter extends ShopInterface {

  public DefaultFooter(InventoryGUI inv) {
    super(inv);

    if (Permissions.GS_CREATEAUCTION.isSetOn(this.inv.getPlayer())) {
      this.actions.put(53, new NewAuction());
      this.togglers.get(53).set();
    }
    this.actions.put(46, new NextInterface("AuctionGlobalView"));
  }

  private void updateBalance() {
    ItemStack item = this.inv.getInv().getItem(45);
    VaultEconomy eco = GlobalMarketChest.plugin.economy;

    ItemStackUtils.setItemStackLore(item,
      Utils.toList(String.format("&3%s",
      eco.getEconomy().format(eco.getMoneyOfPlayer(this.inv.getPlayer().getUniqueId()))
    )));
    this.inv.getInv().setItem(45, item);
  }

  protected void updateAuctionNumber() {
    final ItemStack item = this.inv.getInv().getItem(53);
    final String lore = LangUtils.get("Buttons.NewAuction.Description");
    final Integer maxAuctionNumber = GlobalMarketChest.plugin.getConfigLoader().getConfig().getInt("Options.MaxAuctionByPlayer");
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);

    GlobalMarketChest.plugin.auctionManager.getAuctionNumber(shop.getGroup(), this.inv.getPlayer(), auctionNumber ->  {
      this.inv.getTransaction().put(TransactionKey.PLAYERAUCTIONS, auctionNumber);
      ItemStackUtils.setItemStackLore(item,
        Utils.toList(String.format(lore, auctionNumber, maxAuctionNumber)
      ));
      this.inv.getInv().setItem(53, item);
    });
  }

  @Override
  public void load() {
    super.load();
    this.updateBalance();
    if (Permissions.GS_CREATEAUCTION.isSetOn(this.inv.getPlayer()))
      this.updateAuctionNumber();
  }
}
