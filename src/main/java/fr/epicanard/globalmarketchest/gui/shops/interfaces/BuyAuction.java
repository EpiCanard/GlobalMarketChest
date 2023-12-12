package fr.epicanard.globalmarketchest.gui.shops.interfaces;

import com.google.common.collect.ImmutableMap;
import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.auctions.AuctionLoreConfig;
import fr.epicanard.globalmarketchest.exceptions.WarnException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.InterfaceType;
import fr.epicanard.globalmarketchest.gui.actions.NextInterface;
import fr.epicanard.globalmarketchest.gui.actions.PreviousInterface;
import fr.epicanard.globalmarketchest.gui.actions.ReturnBack;
import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.UndoAuction;
import fr.epicanard.globalmarketchest.listeners.events.MoneyExchangeEvent;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.*;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class BuyAuction extends UndoAuction {

  public BuyAuction(InventoryGUI inv) {
    super(inv);

    this.inv.getTransaction().put(TransactionKey.AUCTION_LORE_CONFIG, AuctionLoreConfig.TOSELL);
    this.isTemp = true;
    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTION_INFO);
    final ItemStack item = DatabaseUtils.deserialize(auction.getItemMeta());
    this.setIcon(item);
    this.actions.put(0, new PreviousInterface());
    this.actions.put(31, this::buyAuction);

    if (Permissions.ADMIN_REMOVEAUCTION.isSetOn(inv.getPlayer()))
      this.togglerManager.setTogglerWithAction(inv.getInv(), 28, this.actions, this::adminRemoveAuction);
    if (canSeeShulkerBoxContent(item))
      this.togglerManager.setTogglerWithAction(inv.getInv(), 34, this.actions, new NextInterface(InterfaceType.SHULKER_BOX_CONTENT));
  }

  @Override
  public void load() {
    super.load();
    final AuctionInfo auction = this.inv.getTransactionValue(TransactionKey.AUCTION_INFO);
    this.setIcon(ItemStackUtils.addItemStackLore(
        DatabaseUtils.deserialize(auction.getItemMeta()), auction.getLore(AuctionLoreConfig.TOSELL, this.inv.getPlayer())
    ));
  }

  @Override
  public void destroy() {
    super.destroy();
    this.inv.getTransaction().remove(TransactionKey.AUCTION_INFO);
    this.inv.getTransaction().remove(TransactionKey.AUCTION_LORE_CONFIG);
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

      final MoneyExchangeEvent event = new MoneyExchangeEvent(
          i.getPlayer().getUniqueId(),
          playerStarter,
          auction.getTotalPrice(),
          auction.getPriceWithoutTax()
      );
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
    final String ownerLang = (GlobalMarketChest.plugin.getTax() > 0) ? "InfoMessages.AcquireAuctionOwnerTax" : "InfoMessages.AcquireAuctionOwner";
    final String langPath = (isOwner) ? ownerLang : "InfoMessages.AcquireAuction";

    final Map<String, Object> mapping = ImmutableMap.<String, Object>builder()
      .put("buyer", anonymousPlayer("Buyer").orElseGet(() -> buyer.getName()))
      .put("quantity", auction.getAmount())
      .put("itemName", ItemStackUtils.getItemStackDisplayName(item))
      .put("price", EconomyUtils.format(auction.getTotalPrice()))
      .put("priceWithoutTax", EconomyUtils.format(auction.getPriceWithoutTax()))
      .put("seller", anonymousPlayer("Seller").orElseGet(() -> PlayerUtils.getPlayerName(auction.getPlayerStarter())))
      .put("tax", EconomyUtils.format(auction.getTaxAmount()))
      .build();

    return LangUtils.format(langPath, mapping);
  }

  private Optional<String> anonymousPlayer(String opt) {
    if (ConfigUtils.getBoolean("Options.Anonymous." + opt, false))
      return Optional.of(LangUtils.getOrElse("Divers.Anonymous", "Anonymous"));
    else
      return Optional.empty();
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
    final World broadcastWorld = shop.getLocation().orElse(buyer.getLocation()).getWorld();
    if (message != null && ConfigUtils.getBoolean("Options.Broadcast.BuyInsideWorld", true)) {
      WorldUtils.broadcast(broadcastWorld, message, Arrays.asList(starter));
    }

    if (starter != null && ConfigUtils.getBoolean("Options.Broadcast.NotifyPlayer", true)) {
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
    inv.loadInterface(InterfaceType.CONFIRM_VIEW);
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

  /**
   * Define if a player can see the shulker box content.
   *
   * @param item Item to analyze
   * @return If a player can see the content
   */
  private static boolean canSeeShulkerBoxContent(final ItemStack item) {
    if (item == null || item.getItemMeta() == null) {
      return false;
    }

    final String displayName = Optional.ofNullable(item.getItemMeta().getDisplayName()).map(String::toLowerCase).orElse("");
    return ShulkerBoxContent.isShulker(item)
      && (
          ConfigUtils.getBoolean("Options.ShulkerBox.SeeContent", true)
          ^ ConfigUtils.getStringList("Options.ShulkerBox.ExceptDisplayNames").stream().anyMatch(except -> displayName.contains(except.toLowerCase()))
      );
  }
}
