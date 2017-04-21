package ru.urururu.tests.chatfuel;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class SynchronizedRpsEnforcer implements RpsEnforcer {
    private long[] lastRequests;
    private int nextPosition = 0;

    public SynchronizedRpsEnforcer(int limit) {
        lastRequests = new long[limit];
    }

    public synchronized boolean allowed() {
        return allowed(System.currentTimeMillis());
    }

    boolean allowed(long millis) {
        if (lastRequests[nextPosition] <= millis) {
            lastRequests[nextPosition] = millis + 1000;
            nextPosition = (nextPosition + 1) % lastRequests.length;
            return true;
        }

        return false;
    }
}
