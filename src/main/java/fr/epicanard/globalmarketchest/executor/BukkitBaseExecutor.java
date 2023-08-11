package fr.epicanard.globalmarketchest.executor;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
public class BukkitBaseExecutor implements BaseExecutor {

    private static boolean terminated = false;

    private final AtomicInteger counter = new AtomicInteger(0);

    private final BukkitScheduler bukkitScheduler = Bukkit.getServer().getScheduler();

    @Override
    public int getExecuteSize() {
        return counter.get();
    }

    @Override
    public CallableThread newThread(Runnable runnable) {
        return new CallableThread(runnable);
    }

    @Override
    public void task(Task task) {
        if (terminated) {
            return;
        }
        counter.incrementAndGet();
        long delayed = task.getDelayed();
        boolean async = task.isAsync();
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } finally {
                    counter.decrementAndGet();
                }
            }
        };
        if (delayed != 0) {
            if (async) {
                bukkitRunnable.runTaskLaterAsynchronously(GlobalMarketChest.plugin, delayed);
            } else {
                bukkitRunnable.runTaskLater(GlobalMarketChest.plugin, delayed);
            }
        } else {
            if (async) {
                bukkitRunnable.runTaskAsynchronously(GlobalMarketChest.plugin);
            } else {
                bukkitRunnable.runTask(GlobalMarketChest.plugin);
            }
        }
    }

    @Override
    public void task(Task task, long t, long t1) {
        if (terminated) {
            return;
        }
        counter.incrementAndGet();
        boolean async = task.isAsync();
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    task.run();
                } finally {
                    counter.decrementAndGet();
                }
            }
        };
        if (async) {
            bukkitRunnable.runTaskTimer(GlobalMarketChest.plugin, t, t1);
        } else {
            bukkitRunnable.runTaskTimerAsynchronously(GlobalMarketChest.plugin, t, t1);
        }
    }

    @Override
    public void task(Task task, World world) {
        task(task);
    }

    @Override
    public void task(Task task, Chunk chunk) {
        task(task);
    }

    @Override
    public void task(Task task, Location location) {
        task(task);
    }

    @Override
    public void shutdown() {
        terminated = true;
        bukkitScheduler.cancelTasks(GlobalMarketChest.plugin);
        counter.set(0);
    }
}
