package fr.epicanard.globalmarketchest.Economy;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import fr.epicanard.globalmarketchest.Exceptions.RequiredPluginException;
import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.Utils.PlayerUtils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

public class VaultEconomy {
  Economy economy;
  Permission permission;
  
  /**
   * Init Economy plugin, check if vault is missing, set economy and permissions
   * @throws RequiredPluginException
   */
  public void initEconomy() throws RequiredPluginException {
    Server server = GlobalMarketChest.plugin.getServer();

    if (server.getPluginManager().getPlugin("Vault") == null)
      throw new RequiredPluginException("Vault");

    RegisteredServiceProvider<Economy> eco = server.getServicesManager().getRegistration(Economy.class);
    if(eco == null || eco.getProvider() == null)
      throw new RequiredPluginException("Vault");
    this.economy = eco.getProvider();

    RegisteredServiceProvider<Permission> perm = server.getServicesManager().getRegistration(Permission.class);
    if(perm == null || perm.getProvider() == null)
      throw new RequiredPluginException("A permission");
    this.permission = perm.getProvider();
  }

  /**
   * Check if player has specific permission for vault plugin
   * @param player
   * @param perm
   * @return
   */
  public Boolean hasPermissions(Player player, String perm) {
    return this.permission.has(player, perm);
  }
  
  /**
   * Getter for economy variable
   * @return
   */
  public Economy getEconomy() {
    return this.economy;
  }
  
  /**
   * Getter for permissions variable  
   * @return
   */
  public Permission getPermission() {
    return this.permission;
  }
  
  /**
   * Check if player has account and create if necessary
   * @param player
   * @param create
   * @return
   */
  public Boolean hasAccount(OfflinePlayer player, Boolean create) {
    Boolean ret = this.economy.hasAccount(player);

    if (!ret && create)
      this.economy.createPlayerAccount(player);
    
    return ret;
  }
  
  /**
   * Give money to player and return if it is a success or not
   * @param playerUUID
   * @param amount
   * @return
   */
  public Boolean giveMoneyToPlayer(UUID playerUUID, double amount) {
    OfflinePlayer player = PlayerUtils.getOfflinePlayer(playerUUID);

    this.hasAccount(player, true);
    EconomyResponse resp = this.economy.depositPlayer(player, amount);

    return resp.transactionSuccess();
  }
  
  /**
   * Take money to player and return if it is a success or not
   * @param playerUUID
   * @param amount
   * @return
   */
  public Boolean takeMoneyToPlayer(UUID playerUUID, double amount) {
    OfflinePlayer player = PlayerUtils.getOfflinePlayer(playerUUID);

    this.hasAccount(player, true);
    EconomyResponse resp = this.economy.withdrawPlayer(player, amount);
    
    return resp.transactionSuccess();
  }

  /**
   * Get balance of a player
   * @param playerUUID
   * @return
   */
  public double getMoneyOfPlayer(UUID playerUUID) {
    OfflinePlayer player = PlayerUtils.getOfflinePlayer(playerUUID);
    
    this.hasAccount(player, true);
    
    return this.economy.getBalance(player);
  }
}
