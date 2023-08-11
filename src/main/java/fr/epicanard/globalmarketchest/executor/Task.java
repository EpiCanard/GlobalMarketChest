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

    public Task synchronously() {
        this.async = false;
        return this;
    }

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
