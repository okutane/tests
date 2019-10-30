package ru.urururu.tests.ctbtih;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class CatHandlerTest {
    @Test
    public void testWhenNoCatsArePassedThenNoThreadsAreCreated() {
        ThreadFactoryWithCounter factory = new ThreadFactoryWithCounter();

        CatHandler handler = new CatHandler(factory) {
            @Override
            protected void doHandle(Cat cat) {
                Assert.fail();
            }
        };

        Assert.assertEquals("invocation count matches", 0, factory.getInvocationCount());
    }

    @Test
    public void testWhenCatIsPassedThenThreadIsCreatedAndHandleIsCalled() throws Exception {
        ThreadFactoryWithCounter factory = new ThreadFactoryWithCounter();
        CyclicBarrier cb = new CyclicBarrier(2);

        CatHandler handler = new CatHandler(factory) {
            @Override
            protected void doHandle(Cat cat) {
                try {
                    cb.await(1, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new RuntimeException(e); // todo: use lombok SneakyThrows in tests?
                }
            }
        };
        handler.handle(new Cat());
        cb.await(1, TimeUnit.SECONDS);

        Assert.assertEquals("invocation count matches", 1, factory.getInvocationCount());
    }

    @Test
    public void testWhenSeveralCatsArePassedThenThreadIsReused() throws Exception {
        ThreadFactoryWithCounter factory = new ThreadFactoryWithCounter();
        CyclicBarrier cb = new CyclicBarrier(2);

        CatHandler handler = new CatHandler(factory) {
            @Override
            protected void doHandle(Cat cat) {
                try {
                    cb.await(1, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new RuntimeException(e); // todo: use lombok SneakyThrows in tests?
                }
            }
        };
        handler.handle(new Cat());
        handler.handle(new Cat());
        cb.await(1, TimeUnit.SECONDS);
        cb.await(1, TimeUnit.SECONDS);

        Assert.assertEquals("invocation count matches", 1, factory.getInvocationCount());
    }

    @Test
    public void testWhenCatsArePassedWithDelayThenMoreThreadsAreCreated() throws Exception {
        ThreadFactoryWithCounter factory = new ThreadFactoryWithCounter();
        CyclicBarrier cb = new CyclicBarrier(2);

        CatHandler handler = new CatHandler(factory) {
            @Override
            protected void doHandle(Cat cat) {
                try {
                    cb.await(1, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new RuntimeException(e); // todo: use lombok SneakyThrows in tests?
                }
            }
        };
        handler.handle(new Cat());
        cb.await(1, TimeUnit.SECONDS);
        handler.handle(new Cat());
        cb.await(1, TimeUnit.SECONDS);

        Assert.assertEquals("invocation count matches", 2, factory.getInvocationCount());
    }

    private static class ThreadFactoryWithCounter implements ThreadFactory {
        private final AtomicInteger invocationCount = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            invocationCount.incrementAndGet();
            return new Thread(r);
        }

        public int getInvocationCount() {
            return invocationCount.get();
        }
    }
}