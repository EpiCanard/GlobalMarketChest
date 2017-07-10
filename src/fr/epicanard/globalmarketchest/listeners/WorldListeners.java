package fr.epicanard.globalmarketchest.listeners;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.epicanard.globalmarketchest.guis.GUIBuilder;

public class WorldListeners implements Listener {
  @EventHandler
  public void onInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Action action = event.getAction();

    if (event.getClickedBlock() != null && action == Action.RIGHT_CLICK_BLOCK) {
      BlockState bs = event.getClickedBlock().getState();
      if (bs instanceof Sign) {
        Sign sign = (Sign) bs;
        if (sign.getLine(0).equals("[GMC]")) {
          GUIBuilder gui = new GUIBuilder(null);
          gui.setIcon(new ItemStack(Material.COMPASS));
          gui.setLeave(true);
          gui.setBack(new ItemStack(Material.LAVA_BUCKET));
          gui.setNew(true);
          gui.setItemTo(12, new ItemStack(Material.FURNACE));
          gui.open(player);
        }
      }
    }
  }
}
