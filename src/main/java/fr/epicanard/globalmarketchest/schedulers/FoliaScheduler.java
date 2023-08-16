package fr.epicanard.globalmarketchest.schedulers;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;

public class FoliaScheduler implements BaseScheduler {

  @Override
  public void runTaskAsync(Runnable runnable) {
    Bukkit.getServer().getAsyncScheduler().runNow(GlobalMarketChest.plugin, t -> runnable.run());
  }

  @Override
  public void runTask(Runnable runnable) {
    Bukkit.getServer().getGlobalRegionScheduler().run(GlobalMarketChest.plugin, t -> runnable.run());
  }

  @Override
  public Task runTaskTimer(Runnable runnable, Integer timer) {
    ScheduledTask task = Bukkit.getServer().getGlobalRegionScheduler().runAtFixedRate(GlobalMarketChest.plugin, t -> runnable.run(), 1, timer);
    return new FoliaTask(task);
  }

  @Override
  public void runTaskLaterAsync(Runnable runnable, Long ticks) {
    Bukkit.getServer().getAsyncScheduler().runDelayed(GlobalMarketChest.plugin, t -> runnable.run(), ticks * 1000 / 20, TimeUnit.MILLISECONDS);
  }

}
