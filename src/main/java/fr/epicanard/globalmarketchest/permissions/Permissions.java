package fr.epicanard.globalmarketchest.permissions;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class that handle permissions of plugin
 */
public enum Permissions {
  ADMIN_DELETESHOP("globalmarketchest.admin.shops.deleteshop"),
  ADMIN_REMOVEAUCTION("globalmarketchest.admin.shops.removeauction"),
  ADMIN_SEEAUCTIONS("globalmarketchest.admin.shops.seeauctions"),
  ADMIN_NEWVERSION("globalmarketchest.admin.newversion"),
  GS_CREATESHOP("globalmarketchest.globalshop.createshop"),
  GS_OPENSHOP("globalmarketchest.globalshop.openshop"),
  GS_SHOP_OPENSHOP("globalmarketchest.globalshop.%s.openshop"),
  GS_CREATEAUCTION("globalmarketchest.globalshop.createauction"),
  GS_SHOP_CREATEAUCTION("globalmarketchest.globalshop.%s.createauction"),
  GS_BUYAUCTION("globalmarketchest.globalshop.buyauction"),
  GS_SHOP_BUYAUCTION("globalmarketchest.globalshop.%s.buyauction"),
  AS_CREATESHOP("globalmarketchest.adminshop.createshop"),
  AS_OPENSHOP("globalmarketchest.adminshop.openshop"),
  AS_SHOP_OPENSHOP("globalmarketchest.adminshop.%s.openshop"),
  CMD("globalmarketchest.commands"),
  CMD_RELOAD("globalmarketchest.commands.reload"),
  CMD_OPEN("globalmarketchest.commands.open"),
  CMD_CREATE("globalmarketchest.commands.create"),
  CMD_DELETE("globalmarketchest.commands.delete"),
  CMD_LIST("globalmarketchest.commands.list"),
  CMD_LIST_DETAIL("globalmarketchest.commands.list.detail"),
  CMD_LIST_TP("globalmarketchest.commands.list.tp"),
  CMD_LIST_SET_TP("globalmarketchest.commands.list.settp"),
  CMD_ADMIN_OPEN("globalmarketchest.admin.commands.open"),
  CMD_ADMIN_CLOSE("globalmarketchest.admin.commands.close"),
  CMD_ADMIN_DELETE("globalmarketchest.admin.commands.delete"),
  CMD_ADMIN_FIX("globalmarketchest.admin.commands.fix");

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
   * @param permission Permission to check
   * @return Return a boolean to define if the permission is set
   */
  public static Boolean isSetOn(Player player, String permission) {
    return player != null && (player.hasPermission(permission) || GlobalMarketChest.plugin.economy.hasPermissions(player, permission));
  }

  /**
   * Define if the permission is set
   *
   * @param player Player on which check the permissions
   * @return Return a boolean to define if the permission is set
   */
  public Boolean isSetOn(Player player) {
    return Permissions.isSetOn(player, this.perm);
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
      return this.isSetOn((Player) sender);
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
   * Define if the permission is set for the specified shop
   *
   * @param player Player on which check the permissions
   * @param shop   Name of shop to add in permission
   * @return Return a boolean to define if the permission is set
   */
  public Boolean isSetOnWithShop(final Player player, final String shop) {
    return Permissions.isSetOn(player, String.format(this.perm, shop));
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
      Permissions.sendMessage(player);
    }

    return isSet;
  }

  /**
   * Send permission denied to the player
   *
   * @param player Player that must receive the message
   */
  public static final void sendMessage(Player player) {
    PlayerUtils.sendMessageConfig(player, "ErrorMessages.PermissionNotAllowed");
  }
}
