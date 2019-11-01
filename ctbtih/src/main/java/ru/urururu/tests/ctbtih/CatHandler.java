package ru.urururu.tests.ctbtih;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public abstract class CatHandler {
    private final ThreadFactory factory;
    private final AtomicInteger unprocessedElementsCount = new AtomicInteger();
    private final ConcurrentLinkedQueue<Cat> queue = new ConcurrentLinkedQueue<>();

    public CatHandler() {
        this(Thread::new);
    }

    protected CatHandler(ThreadFactory factory) {
        this.factory = factory;
    }

    public void handle(Cat cat) {
        queue.offer(cat);
        int unprocessedElementsCountBeforeOffer = unprocessedElementsCount.getAndIncrement();

        if (unprocessedElementsCountBeforeOffer == 0) {
            // the processor has been shutdown, going to do it or never started before, it's
            // safe to start new.
            factory.newThread(this::process).start();
        }

        afterThreadExistanceEnsured();
    }

    protected abstract void doHandle(Cat cat); // todo: hide it?

    void afterThreadExistanceEnsured() {
        // do nothing, used in tests only.
    }

    void processorShuttingDown() {
        // do nothing, used in tests only.
    }

    private void process() {
        int unprocessedElementsCountAfterLastProcess;

        do {
            Cat cat = queue.poll(); // not null
            doHandle(cat);

            unprocessedElementsCountAfterLastProcess = unprocessedElementsCount.decrementAndGet();
        } while (unprocessedElementsCountAfterLastProcess != 0);

        processorShuttingDown();
    }
}
