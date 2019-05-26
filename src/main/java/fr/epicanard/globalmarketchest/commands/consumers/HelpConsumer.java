package fr.epicanard.globalmarketchest.commands.consumers;

import org.bukkit.command.CommandSender;

import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

/**
 * Command that show the help, with only commands available to the player.
 *
 * Command : /globalmarketchest help
 * Permission: globalmarketchest.commands
 */
public class HelpConsumer implements CommandConsumer {

  /**
   * Method called when consumer is executed
   * 
   * @param node Command node
   * @param command Command executed
   * @param sender Command's executor (player or console)
   * @param args Arguments of command
   */
  public Boolean accept(CommandNode node, String command, CommandSender sender, String[] args) {
    PlayerUtils.sendMessageConfig(sender, "Commands.HelpCommand.Usage");
    PlayerUtils.sendMessageConfig(sender, "Commands.HelpCommand.Help");
    PlayerUtils.sendMessageConfig(sender, "Commands.HelpCommand.Version");
    if (Permissions.CMD_RELOAD.isSetOn(sender))
      PlayerUtils.sendMessageConfig(sender, "Commands.HelpCommand.Reload");
    if (Permissions.CMD_LIST.isSetOn(sender)) {
      PlayerUtils.sendMessageConfig(sender, "Commands.HelpCommand.List");
      if (Permissions.CMD_LIST_DETAIL.isSetOn(sender))
        PlayerUtils.sendMessageConfig(sender, "Commands.HelpCommand.Detail");
      if (Permissions.CMD_LIST_TP.isSetOn(sender, false))
        PlayerUtils.sendMessageConfig(sender, "Commands.HelpCommand.TP");
    }
    if (Permissions.CMD_OPEN.isSetOn(sender, false))
      PlayerUtils.sendMessageConfig(sender, "Commands.HelpCommand.Open");
    return true;
  }
}