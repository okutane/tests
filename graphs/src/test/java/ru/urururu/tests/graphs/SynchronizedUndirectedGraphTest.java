package ru.urururu.tests.graphs;

public class SynchronizedUndirectedGraphTest extends UndirectedGraphTest {
    
    @Override
    protected <V> Graph<V> createGraph() {
        return new UndirectedGraph<>(new SynchronizedDirectedGraph<>());
    }
}
