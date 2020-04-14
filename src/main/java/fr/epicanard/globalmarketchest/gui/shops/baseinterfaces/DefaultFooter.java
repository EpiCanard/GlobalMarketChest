package fr.epicanard.globalmarketchest.gui.shops.baseinterfaces;

import com.google.common.collect.ImmutableMap;
import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.NewAuction;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import org.bukkit.inventory.ItemStack;

import static fr.epicanard.globalmarketchest.utils.EconomyUtils.format;
import static fr.epicanard.globalmarketchest.utils.EconomyUtils.getMoneyOfPlayer;
import static fr.epicanard.globalmarketchest.utils.LangUtils.format;

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

    ItemStackUtils.setItemStackLore(item,
        Utils.toList("&3" + format(getMoneyOfPlayer(this.inv.getPlayer().getUniqueId())))
    );
    this.inv.getInv().setItem(45, item);
  }

  protected void updateAuctionNumber() {
    final ItemStack item = this.inv.getInv().getItem(53);
    final Integer maxAuctionNumber = this.inv.getPlayerRankProperties().getMaxAuctionByPlayer();
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);

    GlobalMarketChest.plugin.auctionManager.getAuctionNumber(shop.getGroup(), this.inv.getPlayer(), auctionNumber -> {
      this.inv.getTransaction().put(TransactionKey.PLAYER_AUCTIONS, auctionNumber);
      ItemStackUtils.setItemStackLore(item,
          Utils.toList(format("Buttons.NewAuction.Description", ImmutableMap.of(
              "auctionNumber", auctionNumber,
              "maxAuctionNumber", maxAuctionNumber
          )))
      );
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
