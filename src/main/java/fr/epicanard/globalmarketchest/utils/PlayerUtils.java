package fr.epicanard.globalmarketchest.utils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.WarnException;
import fr.epicanard.globalmarketchest.utils.reflection.VersionSupportUtils;
import lombok.experimental.UtilityClass;

/**
 * Utility Class for player action
 */
@UtilityClass
public class PlayerUtils {

  private static final String prefix = Utils.toColor("&a[GlobalMarketChest]&7 ");
  /**
   * Get a player from is UUID
   */
  public OfflinePlayer getOfflinePlayer(UUID playerUUID) {
    return GlobalMarketChest.plugin.getServer().getOfflinePlayer(playerUUID);
  }

  /**
   * Get the uuid of a player into string
   *
   * @param player
   * @return
   */
  public String getUUIDToString(OfflinePlayer player) {
    return player.getUniqueId().toString();
  }

  /**
   * Get the player name from is uuid in string format
   *
   * @param uuid
   * @return Player name
   */
  public String getPlayerName(String uuid) {
    if (uuid == null)
      return null;
    OfflinePlayer pl = PlayerUtils.getOfflinePlayer(UUID.fromString(uuid));
    if (pl.getName() == null)
      return LangUtils.get("Divers.UnknownPlayer");
    return pl.getName();
  }

  /**
   * Return the prefix to use in front of messages
   * 
   * @return
   */
  public String getPrefix() {
    if (GlobalMarketChest.plugin.getConfigLoader().getConfig().getBoolean("Logs.HidePrefix", false)) {
      return Utils.toColor("&7");
    }
    return PlayerUtils.prefix;
  }

  /**
   * Send a message in chat to a player
   * 
   * @param pl Target player of the message
   * @param message Message to send
   */
  public void sendMessage(Player pl, String message) {
    pl.sendMessage(PlayerUtils.getPrefix() + Utils.toColor(message));
  }

  /**
   * Send a message in chat to a player from a config language variable
   * 
   * @param pl Target player of the message
   * @param path Language variable path
   */
  public void sendMessageConfig(Player pl, String path) {
    pl.sendMessage(PlayerUtils.getPrefix() + LangUtils.get(path));
  }

  /**
   * Send a message to a command sender (can be player and console)
   * 
   * @param pl Target command sender of the message
   * @param message Message to send
   */
  public void sendMessage(CommandSender pl, String message) {
    pl.sendMessage(PlayerUtils.getPrefix() + Utils.toColor(message));
  }

  /**
   * Send a message in chat to a command sender from a config language variable
   * 
   * @param pl Target command sender of the message
   * @param path Language variable path
   */
  public void sendMessageConfig(CommandSender pl, String path) {
    pl.sendMessage(PlayerUtils.getPrefix() + LangUtils.get(path));
  }

  /**
   * Define if there is enough place to add the item inside player inventory
   * 
   * @param i Inventory of the player
   * @param item Itemstack that must be put in
   * @return
   */
  public Boolean hasEnoughPlace(PlayerInventory i, ItemStack item) {
    ItemStack[] items = i.getStorageContents();
    return Arrays.asList(items).stream().reduce(0, (res, val) -> {
      if (val == null)
        return res + item.getMaxStackSize();
      if (val.isSimilar(item))
        return res + (val.getMaxStackSize() - val.getAmount());
      return res;
    }, (s1, s2) -> s1 + s2) >= item.getAmount();
  }

  /**
   * Split an itemStack with too big amount in a group of smaller itemstack
   * 
   * @param items Final list on which add item splitted
   * @param item Item to split
   * @param size Max size allowed for the list
   * @return
   */
  private Boolean addToList(List<ItemStack> items, ItemStack item, Integer size) {
    Integer amount = item.getAmount();

    for (ItemStack it : items) {
      if (it.isSimilar(item)) {
        int amountCanAdd = it.getMaxStackSize() - it.getAmount();
        int amountWillAdd = (amount < amountCanAdd) ? amount : amountCanAdd;
        amount -= amountWillAdd;
        it.setAmount(it.getAmount() + amountWillAdd);
      }
      if (amount <= 0)
        return true;
    }

    Double split = Math.ceil(amount.doubleValue() / Integer.valueOf(item.getMaxStackSize()).doubleValue());
    if (items.size() == size || split.intValue() + items.size() > size)
      return false;

    while (amount > 0) {
      ItemStack it = item.clone();
      it.setAmount((amount > it.getMaxStackSize()) ? it.getMaxStackSize() : amount);
      amount -= it.getAmount();
      items.add(it);
    }
    return true;
  }

  /**
   * Define if there is enough place to add a group of items
   * 
   * @param i Inventory of the player
   * @param itemsAdd Items to add inside inventory
   * @param auctionsToAdd Define de number of items that can be put inside inventory
   * @return
   */
  public Boolean hasEnoughPlace(PlayerInventory i, List<ItemStack> itemsAdd, AtomicInteger auctionsToAdd) {
    ItemStack[] storage = i.getStorageContents();
    List<ItemStack> itemsStorage = Arrays.asList(storage).stream().filter(it -> it != null).map(it -> it.clone()).collect(Collectors.toList());

    for (ItemStack item : itemsAdd) {
      if (!PlayerUtils.addToList(itemsStorage, item, storage.length))
        return false;
      auctionsToAdd.incrementAndGet();
    }
    return true;
  }

  /**
   * Define if there is enough place to add the item inside player inventory
   * It send un warning to player if there is not enough place
   * 
   * @param i Inventory of the player
   * @param item Itemstack that must be put in
   * @throws WarnException
   */
  public void hasEnoughPlaceWarn(PlayerInventory i, ItemStack item) throws WarnException {
    if (!PlayerUtils.hasEnoughPlace(i, item))
      throw new WarnException("NotEnoughSpace");
  }

  /**
   * Check all items in inventory and if one have GMC nbt tag it remove it
   * 
   * @param playerInventory inventory of the player
   */
  public void removeDuplicateItems(Inventory playerInventory) {
    ItemStack[] items = playerInventory.getContents();
    for (int i = 0; i < items.length; i++) {
      if (VersionSupportUtils.getInstance().hasNbtTag(items[i])) {
        playerInventory.setItem(i, null);
      }
    }
  }

  /**
   * Get the head of a player
   * 
   * @param player Offline player
   * @return Return itemstack of player head
   */
  public ItemStack getPlayerHead(OfflinePlayer player) {
    final Material headMaterial = Material.getMaterial((Utils.getVersion() == "1.12") ? "SKULL_ITEM" : "PLAYER_HEAD");
    final ItemStack playerHead = new ItemStack(headMaterial, 1, (short) SkullType.PLAYER.ordinal());
    final SkullMeta headMeta = (SkullMeta) playerHead.getItemMeta();
  
    headMeta.setOwningPlayer(player);
    headMeta.setDisplayName(player.getName());
    playerHead.setItemMeta(headMeta);

    return playerHead;
  }
}
