package ru.urururu.tests.ctbtih;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class CatHandlerTest {
    AtomicInteger handleCount = new AtomicInteger();
    CyclicBarrier barrier = new CyclicBarrier(2);

    @Before
    public void setUp() {
        handleCount.set(0);
        barrier.reset();
    }

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
        CatHandler handler = new CatHandler(factory) {
            @Override
            protected void doHandle(Cat cat) {
                handleCount.incrementAndGet();
                await();
            }
        };
        handler.handle(new Cat());
        await();

        Assert.assertEquals("invocation count matches", 1, factory.getInvocationCount());
    }

    @Test
    public void testWhenSeveralCatsArePassedThenThreadIsReused() throws Exception {
        ThreadFactoryWithCounter factory = new ThreadFactoryWithCounter();

        CatHandler handler = new CatHandler(factory) {
            @Override
            protected void doHandle(Cat cat) {
                handleCount.incrementAndGet();
                await();
            }
        };
        handler.handle(new Cat());
        handler.handle(new Cat());
        await();
        await();

        Assert.assertEquals("invocation count matches", 1, factory.getInvocationCount());
        Assert.assertEquals("handle count matches", 2, handleCount.get());
    }

    @Test
    public void testWhenCatsArePassedWithDelayThenMoreThreadsAreCreated() throws Exception {
        ThreadFactoryWithCounter factory = new ThreadFactoryWithCounter();

        CatHandler handler = new CatHandler(factory) {
            @Override
            protected void doHandle(Cat cat) {
                handleCount.incrementAndGet();
                await();
            }
        };
        handler.handle(new Cat());
        await();
        handler.handle(new Cat());
        await();

        Assert.assertEquals("invocation count matches", 2, factory.getInvocationCount());
        Assert.assertEquals("handle count matches", 2, handleCount.get());
    }

    @Test
    public void testWhenCatIsPassedDuringThreadShutdownItsHandledInNewThread() throws Exception {
        ThreadFactoryWithCounter factory = new ThreadFactoryWithCounter();
        AtomicInteger threadExistanceCount = new AtomicInteger();

        CatHandler handler = new CatHandler(factory) {
            @Override
            protected void doHandle(Cat cat) {
                handleCount.incrementAndGet();
                await();
            }

            @Override
            void processorShuttingDown() {
                await();
            }

            @Override
            void afterThreadExistanceEnsured() {
                if (threadExistanceCount.incrementAndGet() == 2) {
                    await();
                }
            }
        };
        handler.handle(new Cat());
        await();
        handler.handle(new Cat());
        await();

        Assert.assertEquals("invocation count matches", 2, factory.getInvocationCount());
        Assert.assertEquals("handle count matches", 2, handleCount.get());
    }

    private void await() {
        try {
            barrier.await(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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