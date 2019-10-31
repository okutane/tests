package ru.urururu.tests.ctbtih;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public abstract class CatHandler {
    private final ThreadFactory factory;
    private final AtomicInteger size = new AtomicInteger();
    private final ConcurrentLinkedQueue<Cat> queue = new ConcurrentLinkedQueue<>();

    public CatHandler() {
        this(Thread::new);
    }

    protected CatHandler(ThreadFactory factory) {
        this.factory = factory;
    }

    public void handle(Cat cat) {
        queue.offer(cat);
        if (size.getAndIncrement() == 0) {
            // we're first after start (or thread shutdown), let's start new thread
            factory.newThread(this::process).start();
        }

        afterThreadExistanceEnsured();
    }

    protected abstract void doHandle(Cat cat); // todo: hide it?

    void afterThreadExistanceEnsured() {
        // do nothing, used in tests only.
    }

    void beforeThreadHolderReset() {
        // do nothing, used in tests only.
    }

    private void process() {
        int s;
        Cat cat = queue.poll(); // not null
        do {
            s = size.decrementAndGet();
            doHandle(cat);

            if (s < 0) {
                throw new IllegalStateException();
            }
            if (s == 0) {
                beforeThreadHolderReset();
                return;
            }

            cat = queue.poll(); // can be null
        } while (cat != null);

        throw new IllegalStateException();
    }
}
