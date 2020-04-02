package fr.epicanard.globalmarketchest.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import fr.epicanard.globalmarketchest.exceptions.InterfaceLoadException;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.epicanard.globalmarketchest.gui.shops.baseinterfaces.ShopInterface;
import fr.epicanard.globalmarketchest.ranks.RankProperties;
import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.shops.Warning;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.Utils;
import fr.epicanard.globalmarketchest.utils.chat.ChatUtils;
import lombok.Getter;

/**
 * Inventory Class that manage inventory view
 */
public class InventoryGUI {
  @Getter
  private Inventory inv;
  private Deque<ShopInterface> shopStack = new ArrayDeque<ShopInterface>();
  @Getter
  private Map<TransactionKey, Object> transaction = new HashMap<TransactionKey, Object>();
  @Getter
  private Player player;
  @Getter
  private Warning warn;
  @Getter
  private Boolean chatEditing = false;
  private Conversation currentConv;
  private Consumer<String> chatConsumer;
  @Getter
  private RankProperties playerRankProperties;

  public InventoryGUI(Player player) {
    this.player = player;
    this.inv = Bukkit.createInventory(null, 54, Utils.toColor("&2GlobalMarketChest"));
    this.warn = new Warning(this.inv);
    this.playerRankProperties = GlobalMarketChest.plugin.getRanksLoader().getPlayerProperties(player);
  }

  /**
   * Check if the inventory in param is the same as this inventory
   *
   * @param inventory
   *          Inventory to verify
   */
  public Boolean inventoryEquals(Inventory inventory) {
    return this.inv.equals(inventory);
  }

  /**
   * Open current inventory for the specified player
   */
  public void open() {
    player.openInventory(this.inv);
  }

  /**
   * Close current inventory for the specified player
   */
  public void close() {
    this.player.closeInventory();
  }

  /**
   * Stop the "ChatEditing" mode, send the value to consumer
   * and reopen inventory
   *
   * @param value Value to send to consumer
   */
  public void setChatReturn(String value) {
    this.chatEditing = false;
    this.open();
    this.getInterface().updateInterfaceTitle();
    this.player.removePotionEffect(PotionEffectType.BLINDNESS);
    if (value != null) {
      this.chatConsumer.accept(value);
    }
    this.chatConsumer = null;
  }

  /**
   * Close the inventory and set player in "ChatEditing" mode
   * Until chat value is set
   *
   * @param consumer Consumer called when leave the chat
   */
  public void openChat(String path, Consumer<String> consumer) {
    this.chatEditing = true;
    this.close();
    this.player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1), true);
    this.chatConsumer = consumer;
    this.currentConv = ChatUtils.newConversation(this.player, LangUtils.get(path));
    this.currentConv.begin();
  }

  /**
   * Unload temporary interface and come back to principal
   */
  public void unloadTempInterface() {
    if (this.shopStack.isEmpty())
      return;
    ShopInterface peek;
    do {
      this.shopStack.peek().unload();
      this.shopStack.pop().destroy();
      peek = this.shopStack.peek();
    } while (peek != null && peek.isTemp());
    Optional.ofNullable(peek).ifPresent(ShopInterface::load);
  }

  /**
   * Unload last loaded Interface and load the previous one
   */
  public void unloadLastInterface() {
    if (this.shopStack.isEmpty())
      return;
    this.shopStack.peek().unload();
    this.shopStack.pop().destroy();
    Optional.ofNullable(this.shopStack.peek()).ifPresent(ShopInterface::load);
  }

  /**
   * Unload all interface
   */
  public void unloadAllInterface() {
    if (this.shopStack.isEmpty())
      return;
    this.shopStack.peek().unload();
    while (!this.shopStack.isEmpty())
      this.shopStack.pop().destroy();
  }

  /**
   * Load interface with a specific name
   *
   * @param name Interface name
   */
  public void loadInterface(String name) {
    Optional<ShopInterface> lastInterface = Optional.empty();
    try {
      ShopInterface shop = (ShopInterface) Class.forName("fr.epicanard.globalmarketchest.gui.shops.interfaces." + name)
          .getDeclaredConstructor(InventoryGUI.class).newInstance(this);
      lastInterface = Optional.ofNullable(this.shopStack.peek());
      lastInterface.ifPresent(ShopInterface::unload);

      shop.load();
      this.shopStack.push(shop);
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException
        | InvocationTargetException e) {
      e.printStackTrace();
    } catch (InterfaceLoadException e) {
      LoggerUtils.warn(e.getMessage());
      lastInterface.ifPresent(ShopInterface::load);
    }
  }

  /**
   * Get the loaded interface
   */
  public ShopInterface getInterface() {
    return this.shopStack.peek();
  }

  /**
   * Get Transaction Value
   *
   * @param key
   *          Key to get transcation object
   * @return <T> return the object with these key
   */
  @SuppressWarnings("unchecked")
  public <T> T getTransactionValue(TransactionKey key) {
    return (T) this.transaction.get(key);
  }

  /**
   * Destroy all the inventory
   */
  public void destroy() {
    this.unloadAllInterface();
    this.close();
    if (this.chatEditing) {
      this.player.removePotionEffect(PotionEffectType.BLINDNESS);
      this.currentConv.abandon();
    }
  }
}
