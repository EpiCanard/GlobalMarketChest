package fr.epicanard.globalmarketchest.utils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.WarnException;
import lombok.experimental.UtilityClass;

/**
 * Utility Class for player action
 */
@UtilityClass
public class PlayerUtils {
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
  public String getUUIDToString(Player player) {
    return player.getUniqueId().toString();
  }

  /**
   * Get the player name from is uuid in string format
   * 
   * @param uuid
   * @return Player name
   */
  public String getPlayerName(String uuid) {
    return PlayerUtils.getOfflinePlayer(UUID.fromString(uuid)).getName();
  }

  /**
   * Send Message to a player
   * 
   * @param player
   * @param message message to sent to player
   */
  public void sendMessagePlayer(Player pl, String message) {
    pl.sendMessage("[GlobalMarketChest] " + Utils.toColor(message));
  }

  public void sendMessageConfig(Player pl, String path) {
    pl.sendMessage("[GlobalMarketChest] " + LangUtils.get(path));
  }

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

  public void hasEnoughPlaceWarn(PlayerInventory i, ItemStack item) throws WarnException {
    if (!PlayerUtils.hasEnoughPlace(i, item))
      throw new WarnException("NotEnoughSpace");
  }
}
