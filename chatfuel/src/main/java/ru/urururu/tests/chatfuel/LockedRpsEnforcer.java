package ru.urururu.tests.chatfuel;

import java.util.concurrent.locks.Lock;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class LockedRpsEnforcer implements RpsEnforcer {
    private long[] lastRequests;
    private final Lock lock;
    private int nextPosition = 0;

    public LockedRpsEnforcer(int limit, Lock lock) {
        lastRequests = new long[limit];
        this.lock = lock;
    }

    public boolean allowed() {
        lock.lock();
        try {
            return allowed(System.currentTimeMillis());
        } finally {
            lock.unlock();
        }
    }

    boolean allowed(long millis) {
        if (lastRequests[nextPosition] <= millis) {
            lastRequests[nextPosition] = millis + 1000;
            nextPosition = (nextPosition + 1) % lastRequests.length;
            return true;
        }

        return false;
    }
}
