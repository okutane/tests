package ru.urururu.tests.chatfuel;

public interface RpsEnforcer {
    boolean allowed();

    default long whenAllowed() {
        return 0;
    }
}