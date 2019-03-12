package com.xcoder.utilities.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Map cache
 *
 * @author chuck lee
 */
public class MapCache extends ConcurrentHashMap<String, MapCache.Node> implements Runnable {

    /**
     * ConcurrentHashMap 扩容大小
     */
    public static final Integer DEFAULT_INITIAL_CAPACITY = 256;

    /**
     * 获取cas锁默认等待时间
     */
    public static final Long DEFAULT_TRY_LOCK_TIME_OUT = 500L;

    /**
     * 让出线程时间片时间
     */
    public static final Long DEFAULT_AWAIT_TIME = 60L * 1000L;

    /**
     * cas 锁
     */
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     * 线程对象监视器
     */
    private final Condition monitor = lock.newCondition();

    private volatile boolean monitorAwait = true;

    /**
     * 同步代码块对象监视器
     */
    private final Object mutex;

    public MapCache(Object mutex) {
        super(DEFAULT_INITIAL_CAPACITY);
        if (MixedUtensil.objectNull(mutex)) {
            this.mutex = this;
        } else {
            this.mutex = mutex;
        }
    }

    static class Node {
        private final String key;
        private String value;
        private final Long expire;
        volatile Long expireTime;

        public Node(String key, String value, Long expire) {
            MixedUtensil.objectsNullPointerException(key, value, expire);
            this.key = key;
            this.value = value;
            this.expire = expire;
            this.expireTime = getAddExpireTime();
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        private final Long getAddExpireTime() {
            return this.expire + System.currentTimeMillis();
        }

        public final boolean isExpire() {
            return System.currentTimeMillis() > this.expireTime;
        }
    }

    @Override
    public final void run() {
        for (; !Thread.currentThread().isInterrupted(); ) {
            if (this.isEmpty()) {
                this.tryLockAwait(DEFAULT_TRY_LOCK_TIME_OUT, DEFAULT_AWAIT_TIME);
            }

            this.remove("");
        }
    }

    @Override
    public Node put(String key, Node value) {
        final Node v = super.put(key, value);
        this.tryLockSignal(0);
        return v;
    }

    /**
     * tryLockAwait
     *
     * @param timeOut timeOut
     * @param time    time
     */
    private void tryLockAwait(final long timeOut, final long time) {
        try {
            if (lock.tryLock(timeOut, TimeUnit.MILLISECONDS)) {
                this.monitorAwait = true;
                monitor.await(time, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    /**
     * tryLockSignal
     *
     * @param timeOut timeOut
     */
    private void tryLockSignal(final long timeOut) {
        try {
            if (this.monitorAwait) {
                if (lock.tryLock(timeOut, TimeUnit.MILLISECONDS)) {
                    this.monitorAwait = false;
                    monitor.signal();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
}
