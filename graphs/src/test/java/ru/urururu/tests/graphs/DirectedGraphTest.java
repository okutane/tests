package ru.urururu.tests.graphs;

import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;

public abstract class DirectedGraphTest extends GraphTest {

    @Test
    public void testSingleEdgePathNotFound() {
        Graph<String> letters = createGraph();
        letters.addVertex("A");
        letters.addVertex("B");
        letters.addEdge("B", "A");

        Optional<List<Edge<String>>> findResult = letters.getPath("A", "B");

        // todo code above is same for ru.urururu.tests.graphs.UndirectedGraphTest.testSingleEdgeReversePathFound
        assertFalse("path not exists", findResult.isPresent());
    }
}
