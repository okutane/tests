package ru.urururu.tests.graphs;

public class Edge<V> {
    private final V from;
    private final V to;

    public Edge(V from, V to) {
        this.from = from;
        this.to = to;
    }

    public V getFrom() {
        return from;
    }

    public V getTo() {
        return to;
    }
}
