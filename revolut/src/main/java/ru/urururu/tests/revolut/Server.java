package ru.urururu.tests.revolut;

import com.sun.jersey.simple.container.SimpleServerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class Server {
    public static void main(String... args) throws IOException {
        try (Closeable server = SimpleServerFactory.create("http://localhost:5555")) {
            System.in.read();
        }
    }
}
