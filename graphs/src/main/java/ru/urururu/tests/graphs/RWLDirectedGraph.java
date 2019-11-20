package ru.urururu.tests.graphs;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RWLDirectedGraph<V> extends DirectedGraph<V> {
    private final ReadWriteLock lock;

    RWLDirectedGraph(boolean fair) {
        lock = new ReentrantReadWriteLock(fair);
    }

    @Override
    void withWriteLock(Runnable runnable) {
        lock.writeLock().lock();
        try {
            runnable.run();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    <R> R withReadLock(Callable<R> callable) {
        lock.readLock().lock();
        try {
            return callable.call();
        } catch (Exception e) {
            throw new IllegalStateException(e); // no checked exceptions are expected here.
        } finally {
            lock.readLock().unlock();
        }
    }
}
