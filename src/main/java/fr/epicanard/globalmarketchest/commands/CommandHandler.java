package fr.epicanard.globalmarketchest.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import fr.epicanard.globalmarketchest.commands.consumers.CloseConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.DetailConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.FixAuctionsConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.HelpConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.ListConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.OpenConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.ReloadConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.TPConsumer;
import fr.epicanard.globalmarketchest.commands.consumers.VersionConsumer;
import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.WorldUtils;
import org.bukkit.entity.HumanEntity;


public class CommandHandler implements CommandExecutor, TabCompleter {

  private CommandNode command = new CommandNode("globalmarketchest", Permissions.CMD, false, false);

  public CommandHandler() {
    // Help - /globalmarketchest help
    this.command.setCommand(new HelpConsumer());
    this.command.addSubNode(new CommandNode("help", Permissions.CMD, false, false))
      .setCommand(new HelpConsumer());

    // Version - /globalmarketchest version
    this.command.addSubNode(new CommandNode("version", Permissions.CMD, false, false))
      .setCommand(new VersionConsumer());

    // Reload - /globalmarketchest reload
    this.command.addSubNode(new CommandNode("reload", Permissions.CMD_RELOAD, false, false))
      .setCommand(new ReloadConsumer());

    // Open - /globalmarketchest open <shop> [player]
    this.command.addSubNode(
      new CommandNode("open", Permissions.CMD_OPEN, true, false)
      .setCommand(new OpenConsumer())
      .setTabConsumer(this::shopsPlayerTabComplete));

    // Close - /globalmarketchest close <player>
    this.command.addSubNode(
      new CommandNode("close", Permissions.CMD_ADMIN_CLOSE, true, false)
      .setCommand(new CloseConsumer())
      .setTabConsumer(this::playersTabComplete));

    // List - /globalmarketchest list
    CommandNode listNode = new CommandNode("list", Permissions.CMD_LIST, false, false)
      .setCommand(new ListConsumer());
    this.command.addSubNode(listNode);

    // List.Detail - /globalmarketchest list detail <shop>
    CommandNode detailNode = new CommandNode("detail", Permissions.CMD_LIST_DETAIL, true, false)
      .setCommand(new DetailConsumer())
      .setTabConsumer(this::shopsTabComplete);
    listNode.addSubNode(detailNode);

    // List.TP - /globalmarketchest list tp <shop> <position>
    CommandNode tpNode = new CommandNode("tp", Permissions.CMD_LIST_TP, true, true)
      .setCommand(new TPConsumer())
      .setTabConsumer(this::shopIdTabComplete);
    listNode.addSubNode(tpNode);

    // Fix - /globalmarketchest fix
    CommandNode fixNode = new CommandNode("fix", Permissions.CMD_ADMIN_FIX, false, false);
    this.command.addSubNode(fixNode);

    // Fix Auctions - /globalmarketchest fix auctions [type]
    CommandNode fixAuctionsNode = new CommandNode("auctions", Permissions.CMD_ADMIN_FIX, true, false)
        .setCommand(new FixAuctionsConsumer())
        .setTabConsumer((player, args) -> {
          if (args.length == 1)
            return FixAuctionsConsumer.getFixAuctionsType().stream().filter((type) -> type.startsWith(args[0])).collect(Collectors.toList());
          return new ArrayList<>();
        });
    fixNode.addSubNode(fixAuctionsNode);

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

  /**
   * Return a list of shops matching with start of the first element of args param
   *
   * @param sender Commands's executor (player or console)
   * @param args Command arguments
   * @return List of shops matching
   */
  private List<String> shopsTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
      return GlobalMarketChest.plugin.shopManager.getShops().stream()
      .filter(shop -> shop.getGroup().startsWith(args[0]))
      .map(ShopInfo::getGroup)
      .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  /**
   * If the length of args is equal to 1
   *    Return a list of shops matching with start of the first element of args param
   * If the lenght of args is equal to 2
   *    Return a list of shop's position with start of the second element of args param
   *
   * @param sender Commands's executor (player or console)
   * @param args Command arguments
   * @return List of shops matching
   */
  private List<String> shopIdTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1 || args.length == 2) {
      final Stream<ShopInfo> shopsStream = GlobalMarketChest.plugin.shopManager.getShops().stream()
      .filter(ShopInfo::getExists);

      if (args.length == 1) {
        return shopsStream
          .filter(shop ->  shop.getGroup().startsWith(args[0]))
          .map(ShopInfo::getGroup)
          .collect(Collectors.toList());
      }
      return shopsStream
        .filter(shop -> shop.getGroup().equals(args[0]) && Integer.toString(shop.getId()).startsWith(args[1]))
        .map(shop -> WorldUtils.getStringFromLocation(shop.getSignLocation(), ",", true))
        .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  /**
   * Return a list of players matching with start of the first element of args param
   *
   * @param sender Commands's executor (player or console)
   * @param args Command arguments
   * @return List of shops matching
   */
  private List<String> playersTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
      return GlobalMarketChest.plugin.getServer().getOnlinePlayers().stream()
        .map(HumanEntity::getName)
        .filter(name -> name.startsWith(args[0]))
        .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  /**
   * If the length of args is equal to 1
   *    Return a list of shops matching with start of the first element of args param
   * If the lenght of args is equal to 2
   *    Return a list of players matching with start of the first element of args param
   *
   * @param sender Commands's executor (player or console)
   * @param args Command arguments
   * @return List of shops matching
   */
  private List<String> shopsPlayerTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
      return this.shopsTabComplete(sender, args);
    }
    if (args.length == 2 && Permissions.CMD_ADMIN_OPEN.isSetOn(sender)) {
      return this.playersTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
    }
    return new ArrayList<>();
  }
}
