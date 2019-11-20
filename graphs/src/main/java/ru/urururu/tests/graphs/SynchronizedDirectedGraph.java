package ru.urururu.tests.graphs;

import java.util.concurrent.Callable;

public class SynchronizedDirectedGraph<V> extends DirectedGraph<V> {
    private final Object lock = new Object();

    @Override
    void withWriteLock(Runnable runnable) {
        synchronized (lock) {
            runnable.run();
        }
    }

    @Override
    <R> R withReadLock(Callable<R> callable) {
        synchronized (lock) {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new IllegalStateException(e); // no checked exceptions are expected here.
            }
        }
    }
}
