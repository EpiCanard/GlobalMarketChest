package fr.epicanard.globalmarketchest.commands.consumers;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.chat.ChatUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Command that list all shop linked to a shop group name in paramater.
 *
 * Command : /globalmarketchest list detail <shop>
 * Permission: globalmarketchest.commands.list.detail
 */
public class DetailConsumer implements CommandConsumer {

  /**
   * Method called when consumer is executed
   *
   * @param node Command node
   * @param command Command executed
   * @param sender Command's executor (player or console)
   * @param args Arguments of command
   */
  public Boolean accept(CommandNode node, String command, CommandSender sender, String[] args) {
    if (args.length == 0) {
      return node.invalidCommand(sender, command);
    }

    List<ShopInfo> shops = GlobalMarketChest.plugin.shopManager.getShops().stream()
        .filter(shop -> shop.getGroup().equals(args[0])).collect(Collectors.toList());

    if (shops.size() == 0) {
      PlayerUtils.sendMessage(sender, LangUtils.get("ErrorMessages.UnknownShop") + args[0]);
      return false;
    }

    TextComponent message = ChatUtils.newComponent(LangUtils.get("Commands.ListShop"));
    message.setColor(ChatColor.GOLD);

    TextComponent link;
    if (Permissions.CMD_OPEN.isSetOn(sender, false)) {
      link = ChatUtils.createLinkWithBracket(args[0], LangUtils.get("Commands.Buttons.OpenHover"), ChatColor.BLUE,
          "/globalmarketchest open " + args[0]);
    } else {
      link = ChatUtils.createLinkWithBracket(args[0], null, ChatColor.BLUE, null);
    }
    message.addExtra(link);

    for (ShopInfo shop : shops) {
      message.addExtra("\n");
      TextComponent line = ChatUtils.newComponent(" - "
          + ChatColor.DARK_GREEN + shop.getServer() + " "
          + ChatColor.GOLD + shop.getSignLocationString() + " ");

      if (Permissions.CMD_LIST_TP.isSetOn(sender, false) && shop.getExists()) {
        TextComponent linkTP = ChatUtils.createLinkWithBracket(LangUtils.get("Commands.Buttons.TeleportText"),
          LangUtils.get("Commands.Buttons.TeleportHover"), ChatColor.DARK_AQUA,
          String.format("/globalmarketchest list tp %s %s", shop.getGroup(),
          shop.getSignLocationString()));
        line.addExtra(linkTP);
      }
      message.addExtra(line);
    }
    sender.spigot().sendMessage(message);
    return true;
  }
}
