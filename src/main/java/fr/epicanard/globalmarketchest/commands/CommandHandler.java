package fr.epicanard.globalmarketchest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandHandler implements CommandExecutor {

  public CommandHandler() {
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
    if (sender != null && sender instanceof Player) {
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
