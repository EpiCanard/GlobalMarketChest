package fr.epicanard.globalmarketchest.schedulers;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class FoliaTask implements Task {
  private ScheduledTask scheduledTask;

  public FoliaTask(ScheduledTask task) {
    this.scheduledTask = task;
  }

  @Override
  public Boolean isCancelled() {
    return this.scheduledTask.isCancelled();
  }

  @Override
  public void cancel() {
    this.scheduledTask.cancel();
  }
}
