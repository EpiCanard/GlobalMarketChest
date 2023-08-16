package fr.epicanard.globalmarketchest.schedulers;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

@SuppressWarnings("deprecation")
public class SpigotScheduler implements BaseScheduler {
  @Override
  public void runTaskAsync(Runnable runnable) {
    Bukkit.getScheduler().runTaskAsynchronously(GlobalMarketChest.plugin, runnable);
  }

  @Override
  public void runTaskLaterAsync(Runnable runnable, Long ticks) {
    Bukkit.getScheduler().runTaskLaterAsynchronously(GlobalMarketChest.plugin, runnable, ticks);
  }

  @Override
  public void runTask(Runnable runnable) {
    Bukkit.getScheduler().runTask(GlobalMarketChest.plugin, runnable);
  }

  @Override
  public Task runTaskTimer(Runnable runnable, Integer timer) {
    BukkitTask task = Bukkit.getScheduler().runTaskTimer(GlobalMarketChest.plugin, runnable, 0, 10);
    return new SpigotTask(task);
  }

}
