package fr.epicanard.globalmarketchest.executor;

import lombok.Setter;

public class CallableThread extends Thread {
    @Setter
    private ThreadCompleteCallback callback;

    public CallableThread(Runnable target) {
        super(target);
        threadName(null);
    }

    public void threadName(String name) {
        long id = getId();
        name = name == null ? "" : (" [" + name + "]");
        setName("GlobalMarketChest" + name + " thread" + id);
    }

    @Override
    public void run() {
        super.run();
        if (callback != null) {
            callback.done(this);
        }
    }
}