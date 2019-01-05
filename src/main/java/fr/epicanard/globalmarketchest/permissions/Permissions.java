package fr.epicanard.globalmarketchest.permissions;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;

/**
 * Class that handle permissions of plugin
 */
public enum Permissions {
  GS_CREATESHOP("globalmarketchest.globalshop.createshop"),
  GS_OPENSHOP("globalmarketchest.globalshop.openshop"),
  GS_CREATEAUCTION("globalmarketchest.globalshop.createauction"),
  GS_BUYAUCTION("globalmarketchest.globalshop.buyauction"),
  CMD("globalmarketchest.commands"),
  CMD_RELOAD("globalmarketchest.commands.reload"),
  CMD_OPEN("globalmarketchest.commands.open"),
  CMD_LIST("globalmarketchest.commands.list"),
  CMD_LIST_DETAIL("globalmarketchest.commands.list.detail"),
  CMD_LIST_DETAIL_TP("globalmarketchest.commands.list.detail.tp")
  ;

  private String perm;

  /**
   * Enum constructor
   * @param perm String permission
   */
  Permissions(String perm) {
    this.perm = perm;
  }

  /**
   * Define if the permission is set
   *
   * @param player Player on which check the permissions
   * @return Return a boolean to define if the permission is set
   */
  public Boolean isSetOn(Player player) {
    if (player != null && (player.hasPermission(this.perm) ||  GlobalMarketChest.plugin.economy.hasPermissions(player, this.perm)))
      return true;
    return false;
  }

  /**
   * Define if the permission is set on the CommandSender
   * If the CommandSender is not a Player (ex: console) the result is set with the value
   * of param defaultSender
   *
   * @param sender CommandSender on which check the permissions
   * @param defaultSender Value returned if sender is not Player
   * @return Return a boolean to define if the permission is set
   */
  public Boolean isSetOn(CommandSender sender, Boolean defaultSender) {
    if (sender instanceof Player)
      return this.isSetOn((Player)sender);
    return defaultSender;
  }

  /**
   * Define if the permission is set on the CommandSender
   * If the CommandSender is not a Player (ex: console) the result is set to true
   *
   * @param sender CommandSender on which check the permissions
   * @return Return a boolean to define if the permission is set
   */
  public Boolean isSetOn(CommandSender sender) {
    return this.isSetOn(sender, true);
  }


  /**
   * Define if the permission is set and print an error message
   *
   * @param player Player on which check the permissions
   * @return Return a boolean to define if the permission is set
   */
  public Boolean isSetOnWithMessage(Player player) {
    final Boolean isSet = this.isSetOn(player);

    if (!isSet) {
      PlayerUtils.sendMessageConfig(player, "ErrorMessages.PermissionNotAllowed");
    }

    return isSet;
  }

  private String getBasicPerm() {
    String[] perms = this.perm.split(".");
    ArrayUtils.remove(perms, perms.length - 1);
    String basicPerm = String.join(".", perms);

    return basicPerm;
  }

}
