package ru.urururu.tests.chatfuel;

import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class LimitedHttpClient {
    private final int limit;
    private final BlockingQueue<Runnable> tasks = new LinkedBlockingDeque<>();
    private RpsEnforcer rpsEnforcer;
    private Thread[] workers;

    LimitedHttpClient(int limit) {
        this.limit = limit;
        this.rpsEnforcer = new SynchronizedRpsEnforcer(limit);

        int threads = Runtime.getRuntime().availableProcessors();
        workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(this::makeQueuedRequest);

            workers[i].start(); // todo add start() method later.
        }
    }

    public void makeQueuedRequest() {
        while (true) {
            Runnable task = tasks.poll();
            long when = rpsEnforcer.whenAllowed();
            if (System.currentTimeMillis() >= when) {
                task.run();
            } else {
                // sleep and run
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
