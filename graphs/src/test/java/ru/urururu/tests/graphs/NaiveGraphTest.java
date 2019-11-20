package ru.urururu.tests.graphs;

import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class NaiveGraphTest {

    @Test
    public void testNoEdgesTrivialSolution() {
        NaiveGraph<String> letters = new NaiveGraph<>();
        letters.addVertex("A");

        Optional<List<Edge<String>>> findResult = letters.getPath("A", "A");

        assertTrue("path exists", findResult.isPresent());

        List<Edge<String>> path = findResult.get();
        assertEquals("path is empty", 0, path.size());
    }

    @Test
    public void testNoEdgesEmptySolution() {
        NaiveGraph<String> letters = new NaiveGraph<>();
        letters.addVertex("A");
        letters.addVertex("B");

        Optional<List<Edge<String>>> findResult = letters.getPath("A", "B");

        assertFalse("path not exists", findResult.isPresent());
    }

    @Test
    public void testSingleEdgePathNotFound() {
        NaiveGraph<String> letters = new NaiveGraph<>();
        letters.addVertex("A");
        letters.addVertex("B");
        letters.addEdge("B", "A");

        Optional<List<Edge<String>>> findResult = letters.getPath("A", "B");

        assertFalse("path not exists", findResult.isPresent());
    }

    @Test
    public void testSingleEdgeDirectPathFound() {
        NaiveGraph<String> letters = new NaiveGraph<>();
        letters.addVertex("A");
        letters.addVertex("B");
        letters.addEdge("A", "B");

        Optional<List<Edge<String>>> findResult = letters.getPath("A", "B");

        assertTrue("path exists", findResult.isPresent());

        List<Edge<String>> path = findResult.get();
        assertEquals("path length matches", 1, path.size());

        Edge<String> edge = path.get(0);

        assertEquals("edge source matches", "A", edge.getFrom());
        assertEquals("edge destination matches", "B", edge.getTo());
    }
}
