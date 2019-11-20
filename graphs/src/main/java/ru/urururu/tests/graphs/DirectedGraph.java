package ru.urururu.tests.graphs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

public abstract class DirectedGraph<V> implements Graph<V> {
    private final List<V> vertices = new ArrayList<>();
    private final Map<V, Integer> indices = new HashMap<>();

    private final List<BitSet> outboundEdges = new ArrayList<>();

    @Override
    public void addVertex(V vertex) {
        withWriteLock(() -> {
            if (indices.containsKey(vertex)) {
                // vertices is a "set" by definition, so we're leaving our graph unchanged.
                return;
            }

            indices.put(vertex, vertices.size());
            vertices.add(vertex);
            outboundEdges.add(new BitSet());
        });
    }

    @Override
    public void addEdge(V from, V to) {
        withWriteLock(() -> {
            int fromIndex = index(from);
            int toIndex = index(to);

            // edges is a "set" by definition, so we're leaving our graph unchanged.
            outboundEdges.get(fromIndex).set(toIndex);
        });
    }

    @Override
    public Optional<List<Edge<V>>> getPath(V from, V to) {
        return withReadLock(() -> {
            int fromIndex = index(from);
            int toIndex = index(to);

            if (fromIndex == toIndex) {
                return Optional.of(Collections.emptyList()); // already there.
            }

            Optional<List<Integer>> path = findPath(fromIndex, toIndex);
            return path.map(this::toEdges);
        });
    }

    abstract void withWriteLock(Runnable runnable);

    abstract <R> R withReadLock(Callable<R> callable);

    /*
     * @pre visited.cardinality() == path.size()
     * @post visited.cardinality() == path.size()
     */
    private Optional<List<Integer>> findContinuation(int fromIndex, int toIndex, BitSet visited, List<Integer> path) {
        visited.set(fromIndex);
        path.add(fromIndex);

        if (outboundEdges.get(fromIndex).get(toIndex)) {
            // continuation exists.

            path.add(toIndex);
            visited.set(toIndex); // minor useless overhead to keep invariants.

            return Optional.of(path);
        }

        BitSet possibleContinuations = (BitSet) outboundEdges.get(fromIndex).clone(); // clone looks simpler
        possibleContinuations.andNot(visited);

        int possibleContinuation = possibleContinuations.nextSetBit(0);
        while (possibleContinuation != -1) { // if we can reach some of previously unvisited vertices.
            Optional<List<Integer>> continuation = findContinuation(possibleContinuation, toIndex, visited, path);
            if (continuation.isPresent()) {
                return continuation;
            }

            possibleContinuation = possibleContinuations.nextSetBit(possibleContinuation + 1);
        }

        // rollback to previous state
        visited.clear(fromIndex);
        path.remove(path.size() - 1);
        return Optional.empty();
    }

    private Optional<List<Integer>> findPath(int fromIndex, int toIndex) {
        if (outboundEdges.get(fromIndex).get(toIndex)) {
            // direct path exists.
            return Optional.of(Arrays.asList(fromIndex, toIndex));
        }

        BitSet visited = new BitSet();
        List<Integer> path = new ArrayList<>(visited.length());

        return findContinuation(fromIndex, toIndex, visited, path);
    }

    private int index(V vertex) {
        Integer result = indices.get(vertex);

        if (result == null) {
            throw new IllegalArgumentException("no such vertex");
        }

        return result;
    }

    /**
     * @pre path.size() >= 2
     * @post @return.size() == path.size() - 1
     */
    private List<Edge<V>> toEdges(List<Integer> path) {
        List<Edge<V>> result = new ArrayList<>(path.size() - 1);

        Iterator<Integer> indexIterator = path.iterator();
        V from = vertex(indexIterator.next());
        do {
            V to = vertex(indexIterator.next());
            result.add(new Edge<>(from, to));
            from = to;
        } while (indexIterator.hasNext());

        return result;
    }

    /**
     * @pre index >= 0 && index < vertices.size()
     */
    private V vertex(int index) {
        return vertices.get(index);
    }
}
