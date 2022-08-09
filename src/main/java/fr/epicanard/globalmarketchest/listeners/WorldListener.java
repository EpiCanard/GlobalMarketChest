package fr.epicanard.globalmarketchest.listeners;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.MissingMethodException;
import fr.epicanard.globalmarketchest.gui.InventoryGUI;
import fr.epicanard.globalmarketchest.gui.TransactionKey;
import fr.epicanard.globalmarketchest.gui.actions.LeaveShop;
import fr.epicanard.globalmarketchest.permissions.Permissions;
import fr.epicanard.globalmarketchest.shops.ShopInfo;
import fr.epicanard.globalmarketchest.utils.*;
import fr.epicanard.globalmarketchest.utils.annotations.AnnotationCaller;
import fr.epicanard.globalmarketchest.utils.annotations.Version;
import fr.epicanard.globalmarketchest.utils.reflection.ReflectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Sign;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Listener for every world interact like opennin a chest
 */
public class WorldListener implements Listener {
  final List<BlockFace> faces = Arrays.asList(BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);

  /**
   * Get attached block to sign
   *
   * @param block Sign block
   * @return Attached block
   */
  @Version(name = "getAttachedBlock", versions = {"1.12"})
  public Block gettAttachedBlock_1_12(Block block) {
    final Sign sign = (Sign) block.getState().getData();
    return block.getRelative(sign.getAttachedFace());
  }

  /**
   * Get attached block to sign
   *
   * @param block Sign block
   * @return Attached block
   */
  @Version(name = "getAttachedBlock")
  public Block getAttachedBlock_latest(Block block) {
    final BlockData data = (BlockData) ReflectionUtils.invokeMethod(block.getState(), "getBlockData", (Object[]) null);

    if (data instanceof Directional) {
      return block.getRelative(((Directional) data).getFacing().getOppositeFace());
    }
    return block.getRelative(BlockFace.DOWN);
  }

  /**
   * Every break of sign by drop is detect to remove the shop and prevent ghost shop (without sign)
   *
   * @param event Block physics event
   */
  @EventHandler
  public void onBlockPhysics(BlockPhysicsEvent event) {
    final Block block = event.getBlock();

    if (ShopUtils.isSign(block.getType())) {
      try {
        final Block attached = AnnotationCaller.call("getAttachedBlock", this, block);

        if (attached.getType() == Material.AIR && block.hasMetadata(ShopUtils.META_KEY)) {
          final ShopInfo shop = ShopUtils.getShop(block);
          if (GlobalMarketChest.plugin.shopManager.deleteShop(shop)) {
            LoggerUtils.warn(String.format("Shop [%s:%s:%s] has been force deleted caused by a physics event",
              shop.getGroup(), shop.getSignLocation().toString(), PlayerUtils.getPlayerName(shop.getOwner())));
          }
        }
      } catch (MissingMethodException e) {
        e.printStackTrace();
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
    final Block faceBlock = block.getRelative(face);
    if (ShopUtils.isSign(faceBlock.getType()) && faceBlock.hasMetadata(ShopUtils.META_KEY)) {
      try {
        final Block attached = AnnotationCaller.call("getAttachedBlock", this, faceBlock);
        return attached.getLocation().distance(block.getLocation()) == 0;
      } catch (MissingMethodException e) {
        e.printStackTrace();
      }
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
      if (!shop.getOwner().equals(PlayerUtils.getUUIDToString(player))
        && !Permissions.ADMIN_DELETESHOP.isSetOn(player)) {
        Permissions.sendMessage(player);
        event.setCancelled(true);
        return;
      }

      final Boolean asLinkedShop = attached.stream().anyMatch(face -> {
        ShopInfo s = ShopUtils.getShop(block.getRelative(face));
        return s != null && s.getId() != shop.getId();
      });

      if (!asLinkedShop) {
        Function<InventoryGUI, Consumer<Boolean>> deleteConsumer = inv -> {
          return b -> {
            if (b) {
              if (GlobalMarketChest.plugin.shopManager.deleteShop(shop)) {
                PlayerUtils.sendMessageConfig(player, "InfoMessages.ShopDeleted");
                String owner = shop.getOwner();
                LoggerUtils.info(String.format("%s : [%s:%s<%s>]", LangUtils.get("InfoMessages.ShopDeleted"),
                  shop.getSignLocation().toString(), PlayerUtils.getPlayerName(owner), owner));
                block.breakNaturally();
              }
            }
            new LeaveShop().accept(inv);
          };
        };
        ShopUtils.openShop(player, shop, inv -> {
          inv.getTransaction().put(TransactionKey.QUESTION, Pair.of(LangUtils.get("InfoMessages.DeleteShopQuestion"), deleteConsumer.apply(inv)));
          inv.getTransaction().put(TransactionKey.HAS_RETURN, false);
          inv.loadInterface("ConfirmView");
          event.setCancelled(true);
        });
        return;
      }

    }
    if (attached.size() > 0) {
      PlayerUtils.sendMessageConfig(player, "ErrorMessages.CantRemoveBlock");
      event.setCancelled(true);
    }
  }

  /**
   * Event to open shop when clicking on shop sign or sign linked block
   *
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
      if (Permissions.GS_OPENSHOP.isSetOn(player) || Permissions.GS_SHOP_OPENSHOP.isSetOnWithShop(player, shop.getGroup())) {
        ShopUtils.openShop(player, shop, inv -> inv.loadInterface("CategoryView"));
      } else {
        Permissions.sendMessage(player);
      }
    }
  }
}
