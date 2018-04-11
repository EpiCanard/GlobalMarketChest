package fr.epicanard.globalmarketchest.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.epicanard.globalmarketchest.DefaultItems;
import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.GUI.Buttons.Button;
import fr.epicanard.globalmarketchest.Utils.Utils;

public abstract class InventoryGUI {
  private Inventory inv;
  private String name;
  private ItemStack icon = DefaultItems.DEFAULT.getItemStack();
  private Integer numberLines = 6;
  private List<Button> buttons = new ArrayList<Button>();

  public InventoryGUI(String interfaceName, Integer numberLines) {
    if (numberLines != null && numberLines > 0 && numberLines <= 6)
      this.numberLines = numberLines;
    this.name = (interfaceName == null) ? "GlobalMarketChest" : interfaceName;
  }
  
  public void buildInterface() {
    this.inv = Bukkit.createInventory(null, this.numberLines * 9, "�8" + this.name);

    // Init iterface
    /*
    ItemStack item = Utils.getItemStack(GlobalMarketChest.plugin.getConfigLoader().getConfig().getString("Interfaces.Background"));
    if (item == null)
      item = DefaultItems.DEFAULT.getItemStack();
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(" ");
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    item.setItemMeta(meta);
    for (int i = 0; i < this.numberLines * 9; i++) {
      inv.setItem(i, item);
    }
    */
  }

  public Inventory getInventory() {
    return this.inv;
  }
  
  public void open(Player player) {
    player.openInventory(inv);
  }
  
  public Boolean inventoryEquals(Inventory i) {
    return this.inv.equals(i);
  }

  public void setItemTo(int pos, ItemStack item) {
    if (item == null || pos < 0 || pos >= this.numberLines * 9)
      return;
    if (this.inv != null)
      this.inv.setItem(pos, item);
  }
  
  public void setItemTo(int x, int y, ItemStack item) {
    this.setItemTo(y * 9 + x, item);
  }

  public ItemStack getItemTo(int pos) {
    if (pos < 0 || pos >= this.numberLines * 9 || this.inv == null)
      return null;
    return this.inv.getItem(pos);
  }
  
  public ItemStack getItemTo(int x, int y) {
    return this.getItemTo(x + y * 9);
  }
  
  public void setName(String name) {
    this.name = name.replaceAll("&", "�");
  }

  public String getName() {
    return this.name;
  }

  public void setIcon(ItemStack mat) {
    if (mat != null) {
      this.icon = Utils.setItemStackMeta(mat, this.name);
      this.setItemTo(4, this.icon);
    }
  }

  public ItemStack getIcon() {
    return this.icon;
  }

  public void setLeave(Boolean set) {
    ItemStack item = (set == true) ? DefaultItems.LEAVE.getItemStack() : DefaultItems.DEFAULT.getItemStack();
    this.setItemTo(8, item);
  }

  public void setBack(ItemStack item) {
    Utils.setItemStackMeta(item, "Retour", "Retourner à l'inventaire précédent");
    this.setItemTo(0, item);
  }

  public void setNew(Boolean set) {
    ItemStack item = (set == true) ? DefaultItems.NEW.getItemStack() : DefaultItems.DEFAULT.getItemStack();
    if (set == true) {
      ItemMeta met = item.getItemMeta();
      List<String> lores = met.getLore();
      lores.set(0, "§f(1/50)");
      met.setLore(lores);
      item.setItemMeta(met);
    }
    this.setItemTo(53, item);
  }
  
  public List<Button> getButtons() {
    return this.buttons;
  }
  
  protected void addButton(Button but) {
    this.addButton(but, false);
  }
  
  protected void addButton(Button but, Boolean addToGUI) {
    if (but != null) {
      this.buttons.add(but);
      if (addToGUI == true)
        but.addToGUI(this);
    }
  }
}
