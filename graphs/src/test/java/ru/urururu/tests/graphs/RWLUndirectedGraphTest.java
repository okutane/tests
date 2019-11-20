package ru.urururu.tests.graphs;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class RWLUndirectedGraphTest extends UndirectedGraphTest {
    private final boolean fair;

    public RWLUndirectedGraphTest(boolean fair) {
        this.fair = fair;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {false}, {true}});
    }

    @Override
    protected <V> Graph<V> createGraph() {
        return new UndirectedGraph<>(new RWLDirectedGraph<>(fair));
    }
}
