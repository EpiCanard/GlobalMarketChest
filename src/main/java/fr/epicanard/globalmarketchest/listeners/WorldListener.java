package fr.epicanard.globalmarketchest.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Sign;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.LangUtils;
import fr.epicanard.globalmarketchest.utils.LoggerUtils;
import fr.epicanard.globalmarketchest.utils.PlayerUtils;
import fr.epicanard.globalmarketchest.utils.ShopUtils;
import fr.epicanard.globalmarketchest.utils.Utils;

/**
 * Listener for every world interact like opennin a chest
 */
public class WorldListener implements Listener {
  final List<BlockFace> faces = Arrays.asList(BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

  /**
   * Every break of sign by drop is detect to remove the shop and prevent ghost shop (without sign)
   * 
   * @param event Block physics event
   */
  @EventHandler
  public void onBlockPhysics(BlockPhysicsEvent event) {
    Block block = event.getBlock();
    if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN) {
      Sign s = (Sign) block.getState().getData();
      Block attachedBlock = block.getRelative(s.getAttachedFace());
      if (attachedBlock.getType() == Material.AIR && block.hasMetadata(ShopUtils.META_KEY)) {
        ShopInfo shop = ShopUtils.getShop(block);
        if (GlobalMarketChest.plugin.shopManager.deleteShop(shop)) {
          LoggerUtils.warn(String.format("Shop [%s:%s:%s] has been force deleted caused by a physics event", 
            shop.getGroup(), shop.getSignLocation().toString(), PlayerUtils.getPlayerName(shop.getOwner())));
        }
      }
    }

  }

  /**
   * Define if the block at the specific face is a shop sign and if it is attached to the block in parameter
   * 
   * @param block Block that must be break
   * @param face The face to check if there is a sign attached
   * @return Define if the block at the specific face is attached
   */
  private Boolean isAttachedTo(Block block, BlockFace face) {
    Block faceBlock = block.getRelative(face);
    if ((faceBlock.getType() == Material.WALL_SIGN || faceBlock.getType() == Material.SIGN) && faceBlock.hasMetadata(ShopUtils.META_KEY)) {
      Sign s = (Sign) faceBlock.getState().getData();
      return (faceBlock.getRelative(s.getAttachedFace()).getLocation().distance(block.getLocation()) == 0);
    }
    return false;
  }

  /**
   * Event called when a player break a block.
   * - If the block is a shop and the player have permissions it delete the shop
   * - If a sign shop to the block, it is not removed and message is displayed 
   * 
   * @param event Block break event
   */
  @EventHandler
  public void onPlayerBreakBlock(BlockBreakEvent event) {
    Block block = event.getBlock();
    Player player = event.getPlayer();
    List<BlockFace> attached = Utils.filter(faces, face -> isAttachedTo(block, face));
    if (block.hasMetadata(ShopUtils.META_KEY)) {
      ShopInfo shop = ShopUtils.getShop(block);
      if (!shop.getOwner().equals(PlayerUtils.getUUIDToString(player)) &&
        !Permissions.ADMIN_DELETESHOP.isSetOn(player)) {
        Permissions.sendMessage(player);
        event.setCancelled(true);
        return;
      }

      Boolean asLinkedShop = attached.stream().anyMatch(face -> {
        ShopInfo s = ShopUtils.getShop(block.getRelative(face));
        return (s != null && s.getId() != shop.getId());
      });

      if (!asLinkedShop) {
        Consumer<Boolean> deleteConsumer = (b) -> {
          if (b) {
            if (GlobalMarketChest.plugin.shopManager.deleteShop(shop)) {
              PlayerUtils.sendMessageConfig(player, "InfoMessages.ShopDeleted");
              String owner = shop.getOwner();
              LoggerUtils.info(String.format("%s : [%s:%s<%s>]", LangUtils.get("InfoMessages.ShopDeleted"),
                shop.getSignLocation().toString(), PlayerUtils.getPlayerName(owner), owner));
              block.breakNaturally();
            }
          }
        };
        InventoryGUI inv = openShop(player, shop);
        inv.getTransaction().put(TransactionKey.QUESTION, Pair.of(LangUtils.get("InfoMessages.DeleteShopQuestion"), deleteConsumer));
        inv.loadInterface("ConfirmView");
        event.setCancelled(true);
        return;
      }

    }
    if (attached.size() > 0) {
      PlayerUtils.sendMessageConfig(player, "ErrorMessages.CantRemoveBlock");
      event.setCancelled(true);
    }
  }

  /**
   * Open a shop interface for player
   *
   * @param player Player on which open the open the shop
   * @param shop Informations about the shop opened
   * @return
   */
  private InventoryGUI openShop(Player player, ShopInfo shop) {
    if (GlobalMarketChest.plugin.inventories.hasInventory(player.getUniqueId())) {
      if (GlobalMarketChest.plugin.inventories.getInventory(player.getUniqueId()).getChatEditing())
        return null;
      GlobalMarketChest.plugin.inventories.removeInventory(player.getUniqueId());
    }
    InventoryGUI inv = new InventoryGUI(player);
    GlobalMarketChest.plugin.inventories.addInventory(player.getUniqueId(), inv);
    inv.getTransaction().put(TransactionKey.SHOPINFO, shop);
    inv.open();
    return inv;
  }

  /**
   * Event to open shop when clicking on shop sign or sign linked block

   * @param event Player interact event
   */
  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Action action = event.getAction();

    if (event.getClickedBlock() != null && action == Action.RIGHT_CLICK_BLOCK) {
      ShopInfo shop = ShopUtils.getShop(event.getClickedBlock());

      if (shop == null)
        return;
      event.setCancelled(true);
      if (!Permissions.GS_OPENSHOP.isSetOnWithMessage(player)) {
        return;
      }
      InventoryGUI inv = openShop(player, shop);
      inv.loadInterface("CategoryView");
    }
  }
}
