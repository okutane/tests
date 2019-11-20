package ru.urururu.tests.graphs;

public class SynchronizedDirectedGraphTest extends DirectedGraphTest {

    @Override
    protected <V> Graph<V> createGraph() {
        return new SynchronizedDirectedGraph<>();
    }
}
