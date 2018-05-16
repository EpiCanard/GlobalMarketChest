package fr.epicanard.globalmarketchest.permissions;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;

/**
 * 
 * globalmarketchest.localshop.create
 * globalmarketchest.localshop.use_command
 * globalmarketchest.localshop.use
 * globalmarketchest.localshop.
 * globalmarketchest.globalshop.create
 * globalmarketchest.globalshop.use_command
 * globalmarketchest.globalshop.use
 * globalmarketchest.adminshop.create
 * globalmarketchest.globalshop.use_command
 * globalmarketchest.adminshop.use
 *
 */
public enum Permissions {
  GLOBALSHOP_CREATE("globalmarketchest.globalshop.create"),
  GLOBALSHOP_USE_COMMAND("globalmarketchest.globalshop.use_command"),
  GLOBALSHOP_USE("globalmarketchest.globalshop.use"),
  LOCALSHOP_CREATE("globalmarketchest.localshop.create"),
  LOCALSHOP_USE_COMMAND("globalmarketchest.locashop.use_command"),
  LOCALSHOP_USE("globalmarketchest.localshop.use"),
  ADMIN_RELOAD("globalmarketchest.admin.reload"),
  ADMIN_PURGE("globalmarket.chest.admin.purge")
  ;
  
  private String perm;
  
  Permissions(String perm) {
    this.perm = perm;
  }
  
  public Boolean isSetOn(Player player, World world) {
    if (player != null && (this.isSetOn(player) || player.hasPermission(this.perm)))
      return true;
    return false;
  }

  public Boolean isSetOn(Player player) {
    System.out.println(this.perm);
    if (player != null && GlobalMarketChest.plugin.economy.hasPermissions(player, this.perm))
      return true;
    return false;
  }

  public Boolean isSetOnn(Player player) {
    System.out.println(this.perm);    
    if (player != null && player.hasPermission(this.perm))
      return true;
    return false;
  }

  
  private String getBasicPerm() {
    String[] perms = this.perm.split(".");
    ArrayUtils.remove(perms, perms.length - 1);
    String basicPerm = String.join(".", perms);
    System.out.println(basicPerm);

    return basicPerm;
  }

}
