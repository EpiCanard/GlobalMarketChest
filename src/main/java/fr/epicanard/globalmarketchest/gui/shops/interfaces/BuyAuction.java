package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import java.util.Arrays;
import java.util.MissingFormatArgumentException;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.exceptions.WarnException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.actions.ReturnBack;
import fr.epicanard.globalmarketchest.gui.shops.ShopInterface;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.ItemStackUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;

public class BuyAuction extends ShopInterface {

  public BuyAuction(InventoryGUI inv) {
    super(inv);

    this.isTemp = true;
    AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);
    ItemStack item = DatabaseUtils.deserialize(auction.getItemMeta());
    this.setIcon(item);
    this.actions.put(0, new PreviousInterface());
    this.actions.put(31, this::buyAuction);
  }

  @Override
  public void load() {
    super.load();
    AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTIONINFO);
    this.setIcon(ItemStackUtils.addItemStackLore(DatabaseUtils.deserialize(auction.getItemMeta()), auction.getLore(AuctionLoreConfig.TOSELL)));
  }

  /**
   * Renew the selected auction to current date
   *
   * @param i
   */
  private void buyAuction(InventoryGUI i) {
    AuctionInfo auction = i.getTransactionValue(TransactionKey.AUCTIONINFO);
    ItemStack item = DatabaseUtils.deserialize(auction.getItemMeta());

    item.setAmount(auction.getAmount());

    try {
      UUID playerStarter = UUID.fromString(auction.getPlayerStarter());
      if (!PlayerUtils.getOfflinePlayer(playerStarter).hasPlayedBefore()) {
        LoggerUtils.warn(String.format("The player with id : %s doesn't exist but there is still active auctions on his name", auction.getPlayerStarter()));
        LoggerUtils.warn(String.format("Auction ID : %d", auction.getId()));
        throw new WarnException("PlayerDoesntExist");
      }
      if (GlobalMarketChest.plugin.economy.getMoneyOfPlayer(i.getPlayer().getUniqueId()) < auction.getTotalPrice())
        throw new WarnException("NotEnoughMoney");
      PlayerUtils.hasEnoughPlaceWarn(i.getPlayer().getInventory(), item);
      if (!GlobalMarketChest.plugin.auctionManager.buyAuction(auction.getId(), i.getPlayer()))
        throw new WarnException("CantBuyAuction");

      GlobalMarketChest.plugin.economy.exchangeMoney(i.getPlayer().getUniqueId(), playerStarter, auction.getTotalPrice());

      i.getPlayer().getInventory().addItem(ItemStackUtils.splitStack(item, auction.getAmount()));

      this.broadcastMessage(auction, i.getPlayer(), item);

      ReturnBack.execute(null, i);
    } catch (WarnException e) {
      i.getWarn().warn(e.getMessage(), 49);
    }
  }

  /**
   * Format the message that must be broadcasted
   * The message is different if the destination player is the owner or not
   *
   * @param isOwner Define the player is owner of the auctions
   * @param buyer Player that buy the auction
   * @param item ItemStack bought
   */
  private String formatMessage(Boolean isOwner, AuctionInfo auction, Player buyer, ItemStack item) {
    String langVariable = (isOwner) ? "InfoMessages.AcquireAuctionOwner" : "InfoMessages.AcquireAuction";
    try {
      if (isOwner) {
        return String.format(LangUtils.get(langVariable),
          buyer.getName(),
          auction.getAmount(),
          ItemStackUtils.getItemStackDisplayName(item),
          auction.getTotalPrice()
        );
      }
      return String.format(LangUtils.get(langVariable),
        buyer.getName(),
        auction.getAmount(),
        ItemStackUtils.getItemStackDisplayName(item),
        auction.getTotalPrice(),
        PlayerUtils.getPlayerName(auction.getPlayerStarter())
      );
    } catch (MissingFormatArgumentException e) {
      LoggerUtils.warn(String.format("Missing or malformed language variable '%s'. Please add it in language file.", langVariable));
    }
    return null;
  }

  /**
   * Broadcast a message inside server to inform about a purchase
   *
   * @param auction Information of auction
   * @param buyer Player that buy the auction
   * @param item ItemStack bought
   */
  private void broadcastMessage(AuctionInfo auction, Player buyer, ItemStack item) {
    ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOPINFO);

    Player starter = PlayerUtils.getOfflinePlayer(UUID.fromString(auction.getPlayerStarter())).getPlayer();

    String message = formatMessage(false, auction, buyer, item);
    if (message != null && GlobalMarketChest.plugin.getConfigLoader().getConfig().getBoolean("Auctions.BroadcastInsideWorld", true)) {
      WorldUtils.broadcast(shop.getSignLocation().getWorld(), message, Arrays.asList(starter));
    }

    if (starter != null && GlobalMarketChest.plugin.getConfigLoader().getConfig().getBoolean("Auctions.NotifyPlayer", true)) {
      String messageOwner = formatMessage(true, auction, buyer, item);
      if (messageOwner != null)
        PlayerUtils.sendMessage(starter, messageOwner);
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    this.inv.getTransaction().remove(TransactionKey.AUCTIONINFO);
  }
}
