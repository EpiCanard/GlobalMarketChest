package fr.epicanard.globalmarketchest.executor;

public interface ThreadCompleteCallback {
    /**
     * 完成
     *
     * @param thread 线程
     */
    void done(CallableThread thread);
}
