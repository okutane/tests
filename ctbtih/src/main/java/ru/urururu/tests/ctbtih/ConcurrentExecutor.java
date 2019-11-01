package ru.urururu.tests.ctbtih;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class ConcurrentExecutor implements Executor {
    private final ThreadFactory factory;
    private final AtomicInteger unprocessedCommandsCount = new AtomicInteger();
    private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

    public ConcurrentExecutor(ThreadFactory factory) {
        this.factory = factory;
    }

    @Override
    public void execute(Runnable command) {
        queue.offer(command);
        int unprocessedCommandsCountBeforeOffer = unprocessedCommandsCount.getAndIncrement();

        if (unprocessedCommandsCountBeforeOffer == 0) {
            // the processor has been shutdown, going to do it or never started before, it's
            // safe to start new.
            factory.newThread(this::runCommands).start();
        }
    }

    private void runCommands() {
        int unprocessedElementsCountAfterLastProcess;

        do {
            Runnable command = queue.poll(); // not null
            command.run();

            unprocessedElementsCountAfterLastProcess = unprocessedCommandsCount.decrementAndGet();
        } while (unprocessedElementsCountAfterLastProcess != 0);
    }
}
