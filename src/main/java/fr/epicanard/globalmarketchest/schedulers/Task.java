package fr.epicanard.globalmarketchest.schedulers;

public interface Task {
  Boolean isCancelled();

  void cancel();
}
