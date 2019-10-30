package ru.urururu.tests.ctbtih;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public abstract class CatHandler {
    private final ThreadFactory factory;
    private final AtomicReference<Thread> threadHolder = new AtomicReference<>();
    private final ConcurrentLinkedQueue<Cat> queue = new ConcurrentLinkedQueue<>();

    public CatHandler() {
        this(Thread::new);
    }

    protected CatHandler(ThreadFactory factory) {
        this.factory = factory;
    }

    public void handle(Cat cat) {
        queue.offer(cat);
        Thread thread;
        do {
            thread = threadHolder.get();
            if (thread != null) {
                return; // don't need to create (still running after cat has been added to queue or new thread is about to start).
            }
            thread = factory.newThread(this::process);
        } while (!threadHolder.compareAndSet(null, thread));

        thread.start(); // we're owning newly created thread, let's start it.
    }

    protected abstract void doHandle(Cat cat); // todo: hide it?

    private void process() {
        Cat cat = queue.poll(); // not null
        do {
            doHandle(cat);
            cat = queue.poll(); // can be null
        } while (cat != null);

        // nothing to do, time to die
        threadHolder.set(null);
    }
}
