package fr.epicanard.globalmarketchest.schedulers;

public interface BaseScheduler {
  void runTaskAsync(Runnable runnable);

  void runTaskLaterAsync(Runnable runnable, Long ticks);

  void runTask(Runnable runnable);

  Task runTaskTimer(Runnable runnable, Integer timer);
}
