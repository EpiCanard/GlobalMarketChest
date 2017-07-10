package fr.epicanard.globalmarketchest.comands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.guis.GUIBuilder;

public class CommandGMC implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
    if (sender != null && sender instanceof Player) {
      Player player = (Player) sender;

      GUIBuilder gui = new GUIBuilder(null);
      gui.setIcon(new ItemStack(Material.COMPASS));
      gui.setLeave(true);
      gui.setBack(new ItemStack(Material.LAVA_BUCKET));
      gui.setNew(true);
      gui.setItemTo(12, new ItemStack(Material.FURNACE));
      gui.open(player);

      return true;
    }
    return false;
  }

}
