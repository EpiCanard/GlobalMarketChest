package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.math.BigDecimal;
import java.util.List;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.actions.ReturnBack;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

public class CreateAuctionPrice extends ShopInterface {
  private int size = 0;

  public CreateAuctionPrice(InventoryGUI inv) {
    super(inv);
    this.isTemp = true;
    this.actions.put(0, new PreviousInterface());
    this.actions.put(53, this::createAuction);

    List<Double> prices = GlobalMarketChest.plugin.getConfigLoader().getConfig().getDoubleList("Price.Ranges");

    for (int i = 0; i < prices.size() && i < 9; i++) {
      final int j = i;
      this.actions.put(18 + i, iv -> this.setPrice(prices.get(j)));
      this.actions.put(27 + i, iv -> this.setPrice(-1 * prices.get(j)));
    }
  }

  @Override
  public void load() {
    super.load();
    List<Double> prices = GlobalMarketChest.plugin.getConfigLoader().getConfig().getDoubleList("Price.Ranges");
    List<String> items = GlobalMarketChest.plugin.getConfigLoader().getConfig().getStringList("Price.Items");
    ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMPITEM);

    this.size = prices.size();
    String[] lore = this.getLore();
    this.inv.getInv().setItem(4, ItemStackUtils.setItemStackLore(item.clone(), lore));
    for (int i = 0; i < prices.size() && i < 9; i++) {
      ItemStack priceItem = ItemStackUtils.getItemStack(items.get(Utils.getIndex(i, items.size())));
      this.inv.getInv().setItem(18 + i, ItemStackUtils.setItemStackMeta(priceItem, "&a+ " + prices.get(i), lore));
      this.inv.getInv().setItem(27 + i, ItemStackUtils.setItemStackMeta(priceItem.clone(), "&c- " + prices.get(i), lore));
    }
  }

  private void setPrice(double addPrice) {
    AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);

    BigDecimal dec = BigDecimal.valueOf(auction.getPrice()).add(BigDecimal.valueOf(addPrice));
    auction.setPrice((dec.doubleValue() < 0.0) ? 0 : dec.doubleValue());
    this.updatePrice();
  }

  /**
   * Update lore of buttons with auction price
   */
  private void updatePrice() {
    ItemStack item = this.inv.getTransactionValue(TransactionKey.TEMPITEM);
    String[] lore = this.getLore();
    Inventory inventory = this.inv.getInv();

    inventory.setItem(4, ItemStackUtils.setItemStackLore(item.clone(), lore));
    for (int i = 0; i < this.size; i++) {
      inventory.setItem(18 + i, ItemStackUtils.setItemStackLore(inventory.getItem(18 + i), lore));
      inventory.setItem(27 + i, ItemStackUtils.setItemStackLore(inventory.getItem(27 + i), lore));
    }
  }

  /**
   * Get lore with quantity and price for current auction item
   * 
   * @return the lore completed
   */
  private String[] getLore() {
    AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);

    String[] lore = {
      "&7" + LangUtils.get("Divers.Quantity") + " : &6" + auction.getAmount(),
      "&7" + LangUtils.get("Divers.UnitPrice") + " : &6" + auction.getPrice(),
      "&7" + LangUtils.get("Divers.TotalPrice") + " : &6" + BigDecimal.valueOf(auction.getPrice()).multiply(BigDecimal.valueOf(auction.getAmount())).doubleValue()
    };
    return lore;
  }

  private void createAuction(InventoryGUI i) {
    AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);

    Boolean ret = GlobalMarketChest.plugin.auctionManager.createAuction(auction);
    if (!ret)
      this.inv.getWarn().warn("Fail to create auction in database", 49);
    else
      ReturnBack.execute(() -> {
        this.inv.getTransaction().remove(TransactionKey.AUCTIONINFO);
        this.inv.getTransaction().remove(TransactionKey.AUCTIONNUMBER);
        this.inv.getTransaction().remove(TransactionKey.TEMPITEM);
      }, i);
  }

  @Override
  public void unload() {
  }
}
