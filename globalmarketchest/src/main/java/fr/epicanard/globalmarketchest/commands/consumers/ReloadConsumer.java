package fr.epicanard.globalmarketchest.commands.consumers;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.commands.CommandConsumer;
import fr.epicanard.globalmarketchest.commands.CommandNode;
import org.bukkit.command.CommandSender;

/**
 * Command that reload the plugin.
 *
 * Command : /globalmarketchest reload
 * Permission: globalmarketchest.commands.reload
 */
public class ReloadConsumer implements CommandConsumer {

  /**
   * Method called when consumer is executed
   * 
   * @param node Command node
   * @param command Command executed
   * @param sender Command's executor (player or console)
   * @param args Arguments of command
   */
  public Boolean accept(CommandNode node, String command, CommandSender sender, String[] args) {
    GlobalMarketChest.plugin.reload(sender);
    return true;
  }
}
