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
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.commands.SelectShops;
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
      
      for (String ar: args) {
      	System.out.println(ar);
      }
      this.openShops(player, args);
      /*
      if (args.length == 0)
        this.openShops(player, args);
      else {
        switch(args[0]) {
          case "locate":
            this.locateBestShop(player, args);
            break;
          case "reload":
            GlobalMarketChest.plugin.reloadPlugin(player);
            break;
          case "purge":
            GlobalMarketChest.plugin.purgeDatabase(player, args);
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
    YamlConfiguration categories = (YamlConfiguration)GlobalMarketChest.plugin.getConfigLoader().getCategories();

/*
    if (args.length >= 2) {
     String st = categories.getString(args[1] + ".DisplayName");
     if (st != null) {
       Inventory inv = Bukkit.createInventory(null, 54, st);
       List<String> items = categories.getStringList(args[1]+ ".Items");
       Object[] arr = items.toArray();
       System.out.println("yey : " + Item.REGISTRY.get(new MinecraftKey("salut")));
       for (int i = 0; i < arr.length && i < 54; i++) {
         MinecraftKey mk = new MinecraftKey((String)arr[i]);
         ItemStack item = CraftItemStack.asNewCraftStack(Item.REGISTRY.get(mk));         
         inv.setItem(i, item);
       }
       player.openInventory(inv);
     }
    }
*/
/*
    Set<String> keys = categories.getKeys(false);
    Object[] ar = keys.toArray();
    for (int i = 0; i < ar.length; i++) {
      List<String> items = categories.getStringList(ar[i]+ ".Items");
      Object[] arr = items.toArray();
      for (int v = 0; v < arr.length; v++) {
        MinecraftKey mk = new MinecraftKey((String)arr[v]);
        if (Item.REGISTRY.get(mk) == null)
          System.out.println("Unkown " + (String)arr[v]);
      }      
    }
    
    MinecraftKey m = new MinecraftKey("minecraft:log");
    ItemStack item = CraftItemStack.asNewCraftStack(Item.REGISTRY.get(m));
*/    
//    ItemStack it = CraftItemStack.asNewCraftStack(Item.REGISTRY.getId(17));
//    item.setDurability((short) 2);
//    player.getInventory().addItem(item);
//    player.getInventory().addItem(it);
    Object[] ar = Item.REGISTRY.keySet().toArray();
    for(int to = 0; to < ar.length; to++) {
      System.out.println("=> " + ar[to]);
    }
    
    GlobalShop shop = new GlobalShop();
    
    CategoryHandler h = new CategoryHandler((YamlConfiguration)GlobalMarketChest.plugin.getConfigLoader().getCategories());
    String[] cat = h.getCategories();
    for (int i = 0; i < cat.length; i++) {
      shop.setItemTo(Utils.toPos(i % 5 + 2, (i / 5) * 2 + 2), Utils.setItemStackMeta(h.getDisplayItem(cat[i]), h.getDisplayName(cat[i])));
    }
    shop.open(player);
  }

  private void openShops(Player player, String[] args) {
    InventoryGUI gui = new SelectShops(args[0]);

    ItemStack item = new ItemStack(Material.COMPASS);
    ItemMeta met = item.getItemMeta();

    /*
    met.setDisplayName("GlobalShop");
    met.setLore(Arrays.asList("Open the GlobalShop of the current world"));
    item.setItemMeta(met);
    gui.setItemTo(10, item);

    ItemStack ita = new ItemStack(Material.CHEST);
    ItemMeta meta = ita.getItemMeta();

    meta.setDisplayName("LocalsShops");
    meta.setLore(Arrays.asList("Open the LocalsShops of players"));
    ita.setItemMeta(meta);
    gui.setItemTo(13, ita);

    ItemStack itb = new ItemStack(Material.ENDER_CHEST);
    ItemMeta metb = itb.getItemMeta();

    metb.setDisplayName("AdminShop");
    metb.setLore(Arrays.asList("Open the AdminShop"));
    itb.setItemMeta(metb);

    gui.setItemTo(16, itb);
    */
    gui.open(player);
  }
}
