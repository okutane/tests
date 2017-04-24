package ru.urururu.tests.chatfuel;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
@BenchmarkMode(Mode.AverageTime)
public class Benchmarks {
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Benchmarks.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void nonSynchronizedSingleThread() {
        RpsEnforcer enforcer = new NonSynchronizedRpsEnforcer(100000);
        runOnSingleThread(enforcer);
    }

    @Benchmark
    public void synchronizedSingleThread() {
        RpsEnforcer enforcer = new SynchronizedRpsEnforcer(100000);
        runOnSingleThread(enforcer);
    }

    @Benchmark
    public void unfairLockSingleThread() {
        RpsEnforcer enforcer = new LockedRpsEnforcer(100000, new ReentrantLock(false));
        runOnSingleThread(enforcer);
    }

    @Benchmark
    public void fairLockSingleThread() {
        RpsEnforcer enforcer = new LockedRpsEnforcer(100000, new ReentrantLock(true));
        runOnSingleThread(enforcer);
    }

    @Benchmark
    public void atomicSingleThread() {
        RpsEnforcer enforcer = new AtomicRpsEnforcer(100000);
        runOnSingleThread(enforcer);
    }

    @Benchmark
    public void synchronized8Thread() {
        RpsEnforcer enforcer = new SynchronizedRpsEnforcer(100000);
        runOnMultipleThreads(enforcer, 8);
    }

    @Benchmark
    public void unfairLock8Thread() {
        RpsEnforcer enforcer = new LockedRpsEnforcer(100000, new ReentrantLock(false));
        runOnMultipleThreads(enforcer, 8);
    }

    @Benchmark
    public void fairLock8Thread() {
        RpsEnforcer enforcer = new LockedRpsEnforcer(100000, new ReentrantLock(true));
        runOnMultipleThreads(enforcer, 8);
    }

    @Benchmark
    public void atomic8Thread() {
        RpsEnforcer enforcer = new AtomicRpsEnforcer(100000);
        runOnMultipleThreads(enforcer, 8);
    }

    private void runOnSingleThread(RpsEnforcer enforcer) {
        for (int i = 0; i < 100000; i++) {
            enforcer.allowed();
        }
    }

    private void runOnMultipleThreads(RpsEnforcer enforcer, int threads) {
        int requestsPerThread = 100000 / threads;

        Thread[] threadArrays = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            threadArrays[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < requestsPerThread; i++) {
                        enforcer.allowed();
                    }
                }
            });
        }

        Arrays.stream(threadArrays).forEach(Thread::start);
        Arrays.stream(threadArrays).forEach(Benchmarks::join);
    }

    private static void join(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
