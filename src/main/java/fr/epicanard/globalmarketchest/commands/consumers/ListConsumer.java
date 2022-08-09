package fr.epicanard.globalmarketchest.commands.consumers;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.chat.ChatUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Command that list all shop group name.
 *
 * Command : /globalmarketchest list
 * Permission: globalmarketchest.commands.list
 */
public class ListConsumer implements CommandConsumer {

  /**
   * Method called when consumer is executed
   * 
   * @param node Command node
   * @param command Command executed
   * @param sender Command's executor (player or console)
   * @param args Arguments of command
   */
  public Boolean accept(CommandNode node, String command, CommandSender sender, String[] args) {
    final TextComponent message = ChatUtils.newComponent(LangUtils.get("Commands.ListShop"));
    message.setColor(ChatColor.GOLD);
    final List<ShopInfo> shops = GlobalMarketChest.plugin.shopManager.getShops();
    final List<String> groups = new ArrayList<>();

    shops.forEach(e -> {
      if (!groups.contains(e.getGroup()))
        groups.add(e.getGroup());
    });

    for (String shop : groups) {
      message.addExtra("\n");
      TextComponent line = ChatUtils.newComponent(" - " + shop);

      if (Permissions.CMD_OPEN.isSetOn(sender, false)) {
        line.addExtra(ChatUtils.createLinkWithBracket(LangUtils.get("Commands.Buttons.OpenText"), LangUtils.get("Commands.Buttons.OpenHover"),
          ChatColor.DARK_AQUA, "/globalmarketchest open " + shop));
      }
      if (Permissions.CMD_LIST_DETAIL.isSetOn(sender, false)) {
        line.addExtra(ChatUtils.createLinkWithBracket(LangUtils.get("Commands.Buttons.DetailText"), LangUtils.get("Commands.Buttons.DetailHover"),
          ChatColor.GREEN, "/globalmarketchest list detail " + shop));
      }
      message.addExtra(line);
    }
    sender.spigot().sendMessage(message);
    return true;

  }
}
