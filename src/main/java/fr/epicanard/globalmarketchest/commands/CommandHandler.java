package fr.epicanard.globalmarketchest.commands;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.gui.CategoryHandler;
import fr.epicanard.globalmarketchest.GlobalMarketChest;

public class CommandHandler implements CommandExecutor {

  public CommandHandler() {
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
    if (sender != null && sender instanceof Player) {
      Player player = (Player) sender;
      this.locateBestShop(player, args);
      /*
      GUIBuilder gui = new GUIBuilder();
      gui.loadInterface(args[0]);
      player.openInventory(gui.getInv());
      */
      /*
      if (args.length == 0)
        this.openShops(player, args);
      else {
        switch(args[0]) {
          case "locate":
            this.locateBestShop(player, args);
            break;
          case "vau":
            this.testVault(player);
            break;
          case "test":
            this.testPerms(player);
            break;
          default:
            player.sendMessage("Unknown command : " + msg);
            break;
        }
      }
      */
      return false;
    }
    return true;
  }
  
  private void locateBestShop(Player player, String[] args) {
        
    CategoryHandler h = new CategoryHandler((YamlConfiguration)GlobalMarketChest.plugin.getConfigLoader().getCategories());
    Set<String> cat = h.getCategories();
    for (int i = 0; i < cat.size(); i++) {
      //shop.setItemTo(Utils.toPos(i % 5 + 2, (i / 5) * 2 + 2), ItemStackUtils.setItemStackMeta(h.getDisplayItem(cat[i]), h.getDisplayName(cat[i])));
    }
  }

}
