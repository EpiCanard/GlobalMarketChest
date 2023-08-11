package fr.epicanard.globalmarketchest.executor;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public interface BaseExecutor {
    /**
     * 获取执行数量
     *
     * @return int
     */
    int getExecuteSize();

    /**
     * 新线程
     *
     * @param runnable 任务
     * @return {@link CallableThread}
     */
    CallableThread newThread(Runnable runnable);

    /**
     * 新任务
     *
     * @param task 运行任务
     */
    void task(Task task);

    /**
     * 关闭
     */
    void shutdown();

    void task(Task task, long t, long t1);

    void task(Task task, World world);

    void task(Task task, Chunk chunk);

    void task(Task task, Location location);
}

