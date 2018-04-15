package fr.epicanard.globalmarketchest.shops;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.DatabaseException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.utils.WorldUtils;

public class ShopInfo {
  private int id;
  private int owner;
  private ItemStack icon;
  private KindShop kind = KindShop.GLOBALSHOP;
  private InventoryGUI launchInterface;
  private Location signLocation;
  private Location chestLocation;
  private ArrayList<String> signLines = new ArrayList<String>();
  private String worldGroup;
  
  public ShopInfo(ResultSet res) {
    if (res != null) {
      try {
        this.id = res.getInt("shopID");
        this.signLocation = WorldUtils.getLocationFromString(res.getString("signLocation"), this.signLocation, "signLocation");
        this.chestLocation = WorldUtils.getLocationFromString(res.getString("location2"), this.chestLocation, "location2");
        this.signLines.add(res.getString("firstLine"));
        this.signLines.add(res.getString("secondLine"));
        this.worldGroup = res.getString("worldGroup");
        if (res.getInt("ownerID") != 1) {
          this.kind = KindShop.LOCALSHOP;
        }
      } catch (DatabaseException | SQLException e) {
        GlobalMarketChest.plugin.getLogger().log(Level.WARNING, e.getMessage());
      }
      
    }
  }
  
  /*
   * Getter of id value 
   */
  public int getIDShop() {
    return this.id;
  }
  
  /*
   * Setter and Getter for kind value
   */
  public KindShop getKind() {
    return this.kind;
  }

  public void setKind(KindShop kind) {
    this.kind = kind;
  }

  /*
   * Setter and Getter for launch interface
   */
  public InventoryGUI getLaunchInterface() {
    return launchInterface;
  }

  public void setLaunchInterface(InventoryGUI launchInterface) {
    this.launchInterface = launchInterface;
  }

  /*
   * Setter and Getter for Sign Position
   */
  public Location getSignLocation() {
    return signLocation;
  }

  public void setSignLocation(Location signPosition) {
    this.signLocation = signPosition;
  }

  /*
   * Setter and Getter for Chest Position
   */
  public Location getChestLocation() {
    return chestLocation;
  }

  public void setChestLocation(Location chestPosition) {
    this.chestLocation = chestPosition;
  }

  /*
   * Setter and Getter for worldGroup
   */
  public String getWorldGroup() {
    return this.worldGroup;
  }

  public void setWorldGroup(String worldGroup) {
    this.worldGroup = worldGroup;
  }

}
