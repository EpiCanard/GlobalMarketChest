package fr.epicanard.globalmarketchest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.utils.PlayerUtils;


public class CommandHandler implements CommandExecutor {
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
    if (sender != null && sender instanceof Player) {
      PlayerUtils.sendMessagePlayer((Player)sender, "&2 COUCOU :D");
      /*
      switch(args[0]) {
        default:
          player.sendMessage("Unknown command : " + msg);
          break;
      }
      */
      return false;
    }
    return true;
  }
}
