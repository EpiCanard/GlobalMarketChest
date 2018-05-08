package fr.epicanard.globalmarketchest.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;

import fr.epicanard.globalmarketchest.economy.VaultEconomy;
import fr.epicanard.globalmarketchest.gui.CategoryHandler;
import fr.epicanard.globalmarketchest.gui.GUIBuilder;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.shops.GlobalShop;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.utils.Utils;
import fr.epicanard.globalmarketchest.GlobalMarketChest;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.MinecraftKey;

public class CommandGMC implements CommandExecutor {

  public CommandGMC() {
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
  
  private void testPerms(Player player) {
    System.out.println("--------------------------------------");
    System.out.println(Permissions.LOCALSHOP_CREATE.isSetOn(player));
    System.out.println(Permissions.GLOBALSHOP_USE.isSetOn(player));
    System.out.println(Permissions.LOCALSHOP_CREATE.isSetOnn(player));
    System.out.println(Permissions.GLOBALSHOP_USE.isSetOnn(player));
    System.out.println("--------------------------------------");
    GlobalMarketChest.plugin.shopManager.updateShops();
  }
  
  private void testVault(Player player) {
    VaultEconomy eco = GlobalMarketChest.plugin.economy;
    System.out.println("*************************************");
    if (eco.hasAccount(player, false))
      System.out.println("Player " + player.getDisplayName() + " has account");
    else
      eco.hasAccount(player, true);
    System.out.println("Player " + player.getDisplayName() + " has " + eco.getMoneyOfPlayer(player.getUniqueId()));
    System.out.println("Give 50 koins to player " + player.getDisplayName());
    eco.giveMoneyToPlayer(player.getUniqueId(), 50);
    System.out.println("Player " + player.getDisplayName() + " has " + eco.getMoneyOfPlayer(player.getUniqueId()));
    System.out.println("Take 25 koins to player " + player.getDisplayName());
    eco.takeMoneyToPlayer(player.getUniqueId(), 25);
    System.out.println("Player " + player.getDisplayName() + " has " + eco.getMoneyOfPlayer(player.getUniqueId()));
    System.out.println("*************************************");
  }
  
  private void locateBestShop(Player player, String[] args) {
        
    CategoryHandler h = new CategoryHandler((YamlConfiguration)GlobalMarketChest.plugin.getConfigLoader().getCategories());
    String[] cat = h.getCategories();
    for (int i = 0; i < cat.length; i++) {
      //shop.setItemTo(Utils.getInstance().toPos(i % 5 + 2, (i / 5) * 2 + 2), Utils.getInstance().setItemStackMeta(h.getDisplayItem(cat[i]), h.getDisplayName(cat[i])));
    }
  }

}
