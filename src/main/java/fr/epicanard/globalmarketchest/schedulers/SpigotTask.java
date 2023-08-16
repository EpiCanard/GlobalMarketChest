package fr.epicanard.globalmarketchest.schedulers;

import org.bukkit.scheduler.BukkitTask;

public class SpigotTask implements Task {
  private BukkitTask bukkitTask;

  public SpigotTask(BukkitTask task) {
    this.bukkitTask = task;
  }

  @Override
  public Boolean isCancelled() {
    return this.bukkitTask.isCancelled();
  }

  @Override
  public void cancel() {
    this.bukkitTask.cancel();
  }
}
