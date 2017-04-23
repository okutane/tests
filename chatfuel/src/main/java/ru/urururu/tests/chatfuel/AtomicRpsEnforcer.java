package ru.urururu.tests.chatfuel;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class AtomicRpsEnforcer implements RpsEnforcer {
    private final AtomicInteger requestLeft;
    private final ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();

    public AtomicRpsEnforcer(int limit) {
        requestLeft = new AtomicInteger(limit);
    }

    @Override
    public boolean allowed() {
        while (true) {
            int value = requestLeft.get();

            if (value == 0) {
                return false;
            }

            if (requestLeft.compareAndSet(value, value - 1)) {
                queue.add(System.currentTimeMillis());
                return true;
            }
        }
    }
}
