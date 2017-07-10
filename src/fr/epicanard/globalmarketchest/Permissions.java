package fr.epicanard.globalmarketchest;

/**
 * GLOBALCHESTSHOP.PLAYER.CREATE_CHEST_SHOP.<worldGroup>
 * GLOBALCHESTSHOP.PLAYER.VISIT_OTHER_PLAYERS_SHOPS.<worldGroup>
 * GLOBALCHESTSHOP.PLAYER.OPEN_GLOBAL_SHOPS.<worldGroup>
 * GLOBALCHESTSHOP.PLAYER.OPEN_ADMIN_SHOP.<worldGroup>
 * GLOBALCHESTSHOP.PLAYER.BUY_FROM_ADMINSHOP.<worldGroup>
 * GLOBALCHESTSHOP.PLAYER.SELL_TO_ADMINSHOP.<worldGroup>
 * 
 * <-------------------- VIP -------------------->
 * 
 * GLOBALCHESTSHOP.VIP.OPEN_GLOBALSHOP_BY_COMMAND.<worldGroup>
 * GLOBALCHESTSHOP.VIP.CREATE_NEW_AUCTIONS_INSIDE_GLOBALSHOP_COMMAND.<worldGroup>
 * GLOBALCHESTSHOP.VIP.CREATE_NEW_AUCTIONS_INSIDE_BUY_COMMAND.<worldGroup>
 * GLOBALCHESTSHOP.VIP.BUY_COMMAND.<worldGroup>
 * GLOBALCHESTSHOP.VIP.SELL_COMMAND.<worldGroup>
 * GLOBALCHESTSHOP.VIP.EXCEED_AUCTION_LIMIT.<VIP_Group>.<worldGroup>
 * <-------------------- Moderator -------------------->
 * 
 * GLOBALCHESTSHOP.MODERATOR.SEE_PLAYERS_SHOPS.<worldGroup>
 * 
 * <-------------------- Admin -------------------->
 * 
 * GLOBALCHESTSHOP.ADMIN
 * 
 * =================================
 * 
 * 
 * 
 * 
 * globalmarketchest.localshop.create
 * globalmarketchest.localshop.use_command
 * globalmarketchest.localshop.use_chest
 * globalmarketchest.localshop.
 * globalmarketchest.globalshop.create
 * globalmarketchest.globalshop.use
 * globalmarketchest.adminshop.create
 * globalmarketchest.adminshop.use
 *
 */
public enum Permissions {
  CREATE_GLOBALSHOP("globalmarketchest.");
  
  private String perm;
  
  Permissions(String perm) {
    this.perm = perm;
  }
}
