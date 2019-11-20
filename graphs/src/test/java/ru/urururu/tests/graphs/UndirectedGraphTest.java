package ru.urururu.tests.graphs;

import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public abstract class UndirectedGraphTest extends GraphTest {

    @Test
    public void testSingleEdgeReversePathFound() {
        Graph<String> letters = createGraph();
        letters.addVertex("A");
        letters.addVertex("B");
        letters.addEdge("B", "A");

        Optional<List<Edge<String>>> findResult = letters.getPath("A", "B");

        // todo code above is same for ru.urururu.tests.graphs.DirectedGraphTest.testSingleEdgePathNotFound
        assertTrue("path exists", findResult.isPresent());
    }
}
