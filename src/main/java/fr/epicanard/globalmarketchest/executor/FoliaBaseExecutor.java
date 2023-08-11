package fr.epicanard.globalmarketchest.executor;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@NoArgsConstructor
public class FoliaBaseExecutor implements BaseExecutor {

    private static boolean terminated = false;

    private final AtomicInteger counter = new AtomicInteger(0);
    private final AsyncScheduler asyncScheduler = Bukkit.getServer().getAsyncScheduler();
    private final GlobalRegionScheduler globalRegionScheduler = Bukkit.getServer().getGlobalRegionScheduler();

    private final RegionScheduler regionScheduler = Bukkit.getServer().getRegionScheduler();

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
        boolean async = task.isAsync();
        long delayed = task.getDelayed();
        // 构建任务
        Consumer<ScheduledTask> taskConsumer = scheduledTask -> {
            try {
                task.run();
            } finally {
                counter.decrementAndGet();
            }
        };
        // 是否为延迟任务
        if (delayed != 0) {
            if (async) {
                asyncScheduler.runDelayed(GlobalMarketChest.plugin, taskConsumer, delayed * 1000 / 20, TimeUnit.MILLISECONDS);
            } else {
                globalRegionScheduler.runDelayed(GlobalMarketChest.plugin, taskConsumer, delayed);
            }
        } else {
            if (async) {
                asyncScheduler.runNow(GlobalMarketChest.plugin, taskConsumer);
            } else {
                globalRegionScheduler.run(GlobalMarketChest.plugin, taskConsumer);
            }
        }
    }

    @Override
    public void task(Task task, long t, long t1) {
        if (terminated) {
            return;
        }

        counter.incrementAndGet();
        Consumer<ScheduledTask> taskConsumer = scheduledTask -> {
            try {
                task.run();
            } finally {
                counter.decrementAndGet();
            }
        };
        globalRegionScheduler.runAtFixedRate(GlobalMarketChest.plugin, taskConsumer, t, t1);
    }

    @Override
    public void task(Task task, World world) {
        if (terminated) {
            return;
        }
        counter.incrementAndGet();
        boolean async = task.isAsync();
        long delayed = task.getDelayed();
        // 构建任务
        Consumer<ScheduledTask> taskConsumer = scheduledTask -> {
            try {
                task.run();
            } finally {
                counter.decrementAndGet();
            }
        };
        // 是否为延迟任务
        if (delayed != 0) {
            regionScheduler.runDelayed(GlobalMarketChest.plugin, world, 0, 0, taskConsumer, delayed);
        } else {
            regionScheduler.run(GlobalMarketChest.plugin, world, 0, 0, taskConsumer);
        }
    }

    @Override
    public void task(Task task, Chunk chunk) {
        if (terminated) {
            return;
        }
        counter.incrementAndGet();
        boolean async = task.isAsync();
        long delayed = task.getDelayed();
        // 构建任务
        Consumer<ScheduledTask> taskConsumer = scheduledTask -> {
            try {
                task.run();
            } finally {
                counter.decrementAndGet();
            }
        };
        // 是否为延迟任务
        if (delayed != 0) {
            regionScheduler.runDelayed(GlobalMarketChest.plugin, chunk.getWorld(), chunk.getX(), chunk.getZ(), taskConsumer, delayed);
        } else {
            regionScheduler.run(GlobalMarketChest.plugin, chunk.getWorld(), chunk.getX(), chunk.getZ(), taskConsumer);
        }
    }

    @Override
    public void task(Task task, Location location) {
        if (terminated) {
            return;
        }
        counter.incrementAndGet();
        boolean async = task.isAsync();
        long delayed = task.getDelayed();
        // 构建任务
        Consumer<ScheduledTask> taskConsumer = scheduledTask -> {
            try {
                task.run();
            } finally {
                counter.decrementAndGet();
            }
        };
        // 是否为延迟任务
        if (delayed != 0) {
            regionScheduler.runDelayed(GlobalMarketChest.plugin, location, taskConsumer, delayed);
        } else {
            regionScheduler.run(GlobalMarketChest.plugin, location, taskConsumer);
        }
    }

    @Override
    public void shutdown() {
        terminated = true;
        asyncScheduler.cancelTasks(GlobalMarketChest.plugin);
        globalRegionScheduler.cancelTasks(GlobalMarketChest.plugin);
        counter.set(0);
    }
}

