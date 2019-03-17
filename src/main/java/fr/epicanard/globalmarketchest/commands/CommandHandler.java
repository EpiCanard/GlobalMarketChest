package fr.epicanard.globalmarketchest.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import fr.epicanard.globalmarketchest.commands.consumers.DetailConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.HelpConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.ListConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.OpenConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.ReloadConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.TPConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.VersionConsumer;
import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;


public class CommandHandler implements CommandExecutor, TabCompleter {

  private CommandNode command = new CommandNode("globalmarketchest", Permissions.CMD, false, false);

  public CommandHandler() {
    // Help
    this.command.setCommand(new HelpConsumer());
    this.command.addSubNode(new CommandNode("help", Permissions.CMD, false, false))
      .setCommand(new HelpConsumer());

    // Version
    this.command.addSubNode(new CommandNode("version", Permissions.CMD, false, false))
      .setCommand(new VersionConsumer());

    // Reload
    this.command.addSubNode(new CommandNode("reload", Permissions.CMD_RELOAD, false, false))
      .setCommand(new ReloadConsumer());

    // Open
    this.command.addSubNode(
      new CommandNode("open", Permissions.CMD_OPEN, true, true)
      .setCommand(new OpenConsumer())
      .setTabConsumer(this::shopsTabComplete));

    // List
    CommandNode listNode = new CommandNode("list", Permissions.CMD_LIST, false, false)
      .setCommand(new ListConsumer());
    this.command.addSubNode(listNode);

    //List.Detail
    CommandNode detailNode = new CommandNode("detail", Permissions.CMD_LIST_DETAIL, true, false)
      .setCommand(new DetailConsumer())
      .setTabConsumer(this::shopsTabComplete);
    listNode.addSubNode(detailNode);

    //List.TP
    CommandNode tpNode = new CommandNode("tp", Permissions.CMD_LIST_DETAIL_TP, true, true)
      .setCommand(new TPConsumer())
      .setTabConsumer(this::shopIdTabComplete);
    listNode.addSubNode(tpNode);
  }

  /**
   * Override onCommand from CommandExecutor - Execute the command globalmarketchest
   */
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
    Boolean succeed = this.command.execute(String.format("/%s %s", msg, StringUtils.join(args, " ")), sender, args);
    if (!succeed) {
      PlayerUtils.sendMessageConfig(sender, "Commands.SeeHelp");
    }
    return succeed;
  }

  /**
   * Override onTabComplete from TabCompleter - return the list of parameters for tab completion
   */
  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
    return this.command.onTabComplete(sender, args);
  }

  private List<String> shopsTabComplete(String[] args) {
    return GlobalMarketChest.plugin.shopManager.getShops().stream()
      .filter(shop -> shop.getGroup().startsWith(args[0]))
      .map(shop -> shop.getGroup())
      .collect(Collectors.toList());
  }

  private List<String> shopIdTabComplete(String[] args) {
    if (args.length == 1) {
      return this.shopsTabComplete(args);
    }
    if (args.length == 2) {
      return GlobalMarketChest.plugin.shopManager.getShops().stream()
      .filter(shop -> shop.getGroup().equals(args[0]) && Integer.toString(shop.getId()).startsWith(args[1]))
      .map(shop -> WorldUtils.getStringFromLocation(shop.getSignLocation(), ",", true))
      .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }
}
