package ru.urururu.tests.chatfuel;

import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class LimitedHttpClient {
    private final int limit;
    private int currentRps = 0;
    private Queue<Long> releaseTimes = new ConcurrentLinkedQueue<>();
    private final BlockingQueue<Runnable> tasks = new LinkedBlockingDeque<>();
    private final Thread[] workers;
    private final Thread cleaner;

    private final Object rpsLock = new Object();

    LimitedHttpClient(int limit) {
        this.limit = limit;

        int threads = 1;//Runtime.getRuntime().availableProcessors();
        workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(this::makeQueuedRequest);

            workers[i].start(); // todo add start() method later.
        }

        cleaner = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long releaseAt = releaseTimes.poll();
                    long timeNow = System.currentTimeMillis();
                    if (timeNow < releaseAt) {
                        try {
                            Thread.sleep(releaseAt - timeNow);
                        } catch (InterruptedException e) {
                            return; // todo redesign if necessary
                        }
                    }

                    synchronized (rpsLock) {
                        currentRps--;
                        rpsLock.notify();
                    }
                }
            }
        });
        cleaner.start();
    }

    public void makeQueuedRequest() {
        while (true) {
            Runnable task = tasks.poll();

            synchronized (rpsLock) {
                while (currentRps >= limit) {
                    try {
                        rpsLock.wait();
                    } catch (InterruptedException e) {
                        return; // todo redesign
                    }
                }

                currentRps++;
            }

            try {
                task.run();
            } finally {
                long releaseAt = System.currentTimeMillis() + 1000;
                releaseTimes.add(releaseAt);
            }
        }
    }

    public void makeRequest(String uri) {
        tasks.add(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = HttpClient.New(new URL(uri)).getInputStream();
                    // todo
                } catch (IOException e) {
                    e.printStackTrace(); // todo
                }
            }
        });
    }
}
