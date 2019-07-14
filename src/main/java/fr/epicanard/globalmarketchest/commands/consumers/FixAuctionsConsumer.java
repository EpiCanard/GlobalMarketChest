package fr.epicanard.globalmarketchest.commands.consumers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.auctions.AuctionInfo;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import fr.epicanard.globalmarketchest.utils.DatabaseUtils;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;

/**
 * Command that convert old minecraft itemMeta to new minecraft version
 *
 * Command : /globalmarketchest fix auctions [active | all]
 * Permission: globalmarketchest.admin.commands.fix
 */
public class FixAuctionsConsumer implements CommandConsumer {

  private final static String FIX_ACTIVE = "active";
  private final static String FIX_ALL = "all";

  public static List<String> getFixAuctionsType() {
    return Arrays.asList(
      FIX_ACTIVE,
      FIX_ALL
    );
  }

  /**
   * Method called when consumer is executed
   *
   * @param node Command node
   * @param command Command executed
   * @param sender Command's executor (player or console)
   * @param args Arguments of command
   */
  public Boolean accept(CommandNode node, String command, CommandSender sender, String[] args) {
    final String fixType = (args.length == 0) ? FIX_ACTIVE : args[0];

    switch (fixType) {
      case FIX_ACTIVE:
        this.updateAuctions(false, sender);
        break;
      case FIX_ALL:
        this.updateAuctions(true, sender);
        break;
      default:
        return false;
    }
    return true;

  }

  /**
   * Update all auctions to convert from old auctions to new
   * It run execution asynchronously
   *
   * @param all Define if it convert alsol history
   * @param sender Command Sender that executed the command
   */
  private void updateAuctions(Boolean all, CommandSender sender) {
    PlayerUtils.sendMessageAndConsole(sender, String.format(LangUtils.get("InfoMessages.ConversionMode"), (all) ? FIX_ALL : FIX_ACTIVE));

    Bukkit.getScheduler().runTaskAsynchronously(GlobalMarketChest.plugin, () -> {
      GlobalMarketChest.plugin.auctionManager.getAllAuctions(all, (auctions) -> {
        final Map<String, List<AuctionInfo>> auctionsMap = auctions.stream().collect(Collectors.groupingBy(AuctionInfo::getGroup));

        auctionsMap.forEach((group, aucts) -> this.convertShopAuctions(group, aucts, sender));
      });
    });
  }

  /**
   * Convert auctions of one shop kind
   *
   * @param group Shop group name
   * @param auctions List of auction of current shop group
   * @param sender Command Sender that executed the command
   */
  private void convertShopAuctions(String group, List<AuctionInfo> auctions, CommandSender sender) {
    PlayerUtils.sendMessageAndConsole(sender, String.format(LangUtils.get("InfoMessages.ConvertingShopAuctions"), group));
    ShopUtils.lockShop(group);

    final List<AuctionInfo> toUpdate = auctions.stream().map(auction -> {
      final ItemStack item = DatabaseUtils.deserialize(auction.getItemMeta());
      if (item == null)
        return null;

      final String itemMeta = DatabaseUtils.serialize(item);

      if (itemMeta.equals(auction.getItemMeta()))
        return null;
      auction.setItemMeta(itemMeta);
      return auction;
    }).filter(Objects::nonNull).collect(Collectors.toList());

    GlobalMarketChest.plugin.auctionManager.updateGroupOfAuctionsMetadata(toUpdate);

    ShopUtils.unlockShop(group);

    PlayerUtils.sendMessageAndConsole(sender, String.format(LangUtils.get("InfoMessages.ShopAuctionsConverted"), group, toUpdate.size(), auctions.size()));
  }
}