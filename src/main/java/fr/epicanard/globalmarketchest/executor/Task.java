package fr.epicanard.globalmarketchest.executor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Task implements Runnable {
    @Getter
    private boolean async = true;

    @Getter
    private long delayed = 0;

    private final Runnable runnable;

    /**
     * 同步任务
     *
     * @return {@link Task}
     */
    public Task synchronously() {
        this.async = false;
        return this;
    }

    /**
     * 延迟
     *
     * @param mills 毫秒
     * @return {@link Task}
     */
    public Task delayed(long mills) {
        this.delayed = mills;
        return this;
    }

    @Override
    public void run() {
        if (runnable != null) {
            runnable.run();
        }
    }
}
