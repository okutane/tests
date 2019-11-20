package ru.urururu.tests.graphs;

import java.util.List;
import java.util.Optional;

public class UndirectedGraph<V> implements Graph<V> {
    private final DirectedGraph<V> inner;

    public UndirectedGraph(DirectedGraph<V> inner) {
        this.inner = inner;
    }

    @Override
    public void addVertex(V vertex) {
        inner.addVertex(vertex);
    }

    @Override
    public void addEdge(V from, V to) {
        inner.withWriteLock(() -> {
            inner.addEdge(from, to);
            inner.addEdge(to, from);
        });
    }

    @Override
    public Optional<List<Edge<V>>> getPath(V from, V to) {
        return inner.getPath(from, to);
    }
}
