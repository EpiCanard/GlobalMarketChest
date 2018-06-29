package fr.epicanard.globalmarketchest.utils;

import java.util.Arrays;
import java.util.UUID;

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
      return res;
    }, (s1, s2) -> s1 + s2) >= item.getAmount();
  }

  public void hasEnoughPlaceWarn(PlayerInventory i, ItemStack item) throws WarnException {
    if (!PlayerUtils.hasEnoughPlace(i, item))
      throw new WarnException("NotEnoughSpace");
  }
}
