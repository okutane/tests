package ru.urururu.tests.chatfuel;

public class RpsEnforcer {
    private long[] lastRequests;
    private int nextPosition = 0;

    public RpsEnforcer(int limit) {
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