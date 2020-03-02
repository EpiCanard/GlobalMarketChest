package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.exceptions.WarnException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.actions.ReturnBack;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.UndoAuction;
import fr.epicanard.globalmarketchest.listeners.events.MoneyExchangeEvent;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.*;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.MissingFormatArgumentException;
import java.util.UUID;
import java.util.function.Consumer;

import static fr.epicanard.globalmarketchest.utils.EconomyUtils.format;

public class BuyAuction extends UndoAuction {

  public BuyAuction(InventoryGUI inv) {
    super(inv);

    this.isTemp = true;
    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTION_INFO);
    final ItemStack item = DatabaseUtils.deserialize(auction.getItemMeta());
    this.setIcon(item);
    this.actions.put(0, new PreviousInterface());
    this.actions.put(31, this::buyAuction);

    if (Permissions.ADMIN_REMOVEAUCTION.isSetOn(inv.getPlayer())) {
      this.togglers.get(28).set();
      this.actions.put(28, this::adminRemoveAuction);
    }
  }

  @Override
  public void load() {
    super.load();
    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTION_INFO);
    this.setIcon(ItemStackUtils.addItemStackLore(DatabaseUtils.deserialize(auction.getItemMeta()), auction.getLore(AuctionLoreConfig.TOSELL)));
  }

  /**
   * Renew the selected auction to current date
   *
   * @param i
   */
  private void buyAuction(InventoryGUI i) {
    final AuctionInfo auction = i.getTransactionValue(TransactionKey.AUCTION_INFO);
    final ItemStack item = DatabaseUtils.deserialize(auction.getItemMeta());

    item.setAmount(auction.getAmount());

    try {
      UUID playerStarter = UUID.fromString(auction.getPlayerStarter());
      if (PlayerUtils.getOfflinePlayer(playerStarter).getFirstPlayed() == 0) {
        LoggerUtils.warn(String.format("The player with id : %s doesn't exist but there is still active auctions on his name", auction.getPlayerStarter()));
        LoggerUtils.warn(String.format("Auction ID : %d", auction.getId()));
        throw new WarnException("PlayerDoesntExist");
      }
      if (GlobalMarketChest.plugin.economy.getMoneyOfPlayer(i.getPlayer().getUniqueId()) < auction.getTotalPrice())
        throw new WarnException("NotEnoughMoney");
      PlayerUtils.hasEnoughPlaceWarn(i.getPlayer().getInventory(), item);
      if (!GlobalMarketChest.plugin.auctionManager.buyAuction(auction.getId(), i.getPlayer()))
        throw new WarnException("CantBuyAuction");

      final MoneyExchangeEvent event = new MoneyExchangeEvent(i.getPlayer().getUniqueId(), playerStarter, auction.getTotalPrice());
      Bukkit.getServer().getPluginManager().callEvent(event);

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
   * @param buyer   Player that buy the auction
   * @param item    ItemStack bought
   */
  private String formatMessage(Boolean isOwner, AuctionInfo auction, Player buyer, ItemStack item) {
    final String langVariable = (isOwner) ? "InfoMessages.AcquireAuctionOwner" : "InfoMessages.AcquireAuction";

    try {
      if (isOwner) {
        return String.format(LangUtils.get(langVariable),
            buyer.getName(),
            auction.getAmount(),
            ItemStackUtils.getItemStackDisplayName(item),
            format(auction.getTotalPrice())
        );
      }
      return String.format(LangUtils.get(langVariable),
          buyer.getName(),
          auction.getAmount(),
          ItemStackUtils.getItemStackDisplayName(item),
          format(auction.getTotalPrice()),
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
   * @param buyer   Player that buy the auction
   * @param item    ItemStack bought
   */
  private void broadcastMessage(AuctionInfo auction, Player buyer, ItemStack item) {
    final ShopInfo shop = this.inv.getTransactionValue(TransactionKey.SHOP_INFO);

    final Player starter = PlayerUtils.getOfflinePlayer(UUID.fromString(auction.getPlayerStarter())).getPlayer();

    final String message = formatMessage(false, auction, buyer, item);
    if (message != null && ConfigUtils.getBoolean("Options.BroadcastInsideWorld", true)) {
      WorldUtils.broadcast(shop.getSignLocation().getWorld(), message, Arrays.asList(starter));
    }

    if (starter != null && ConfigUtils.getBoolean("Options.NotifyPlayer", true)) {
      final String messageOwner = formatMessage(true, auction, buyer, item);
      if (messageOwner != null)
        PlayerUtils.sendMessage(starter, messageOwner);
    }
  }

  /**
   * Action consumer to remove the auction for an admin
   *
   * @param i Inventory clicked
   */
  private void adminRemoveAuction(InventoryGUI i) {
    final Consumer<Boolean> removeAuction = this::removeAuction;
    inv.getTransaction().put(TransactionKey.QUESTION, Pair.of("Are you sure to delete this auction ?", removeAuction));
    inv.loadInterface("ConfirmView");
  }

  /**
   * Callback that remove auction if necessary and reload the interface
   *
   * @param remove Boolean that define if auction must be removed
   */
  private void removeAuction(Boolean remove) {
    if (remove) {
      this.undoAuction(this.inv, ConfigUtils.getBoolean("Options.AdminRemoveAuctionGetItems", true), true);
    } else {
      inv.unloadLastInterface();
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    this.inv.getTransaction().remove(TransactionKey.AUCTION_INFO);
  }
}
