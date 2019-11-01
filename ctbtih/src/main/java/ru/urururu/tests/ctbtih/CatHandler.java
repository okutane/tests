package ru.urururu.tests.ctbtih;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public abstract class CatHandler {
    private final Executor executor;

    public CatHandler() {
        this(Thread::new);
    }

    protected CatHandler(ThreadFactory factory) {
        this.executor = new ConcurrentExecutor(factory);
    }

    public void handle(Cat cat) {
        executor.execute(() -> doHandle(cat));
    }

    protected abstract void doHandle(Cat cat); // todo: hide it?
}
