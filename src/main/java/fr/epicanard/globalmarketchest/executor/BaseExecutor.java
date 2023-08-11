package fr.epicanard.globalmarketchest.executor;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public interface BaseExecutor {

    int getExecuteSize();

    CallableThread newThread(Runnable runnable);

    void task(Task task);

    void shutdown();

    void task(Task task, long t, long t1);

    void task(Task task, World world);

    void task(Task task, Chunk chunk);

    void task(Task task, Location location);
}

