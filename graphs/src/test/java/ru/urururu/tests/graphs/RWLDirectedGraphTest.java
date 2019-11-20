package ru.urururu.tests.graphs;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class RWLDirectedGraphTest extends DirectedGraphTest {
    private final boolean fair;

    public RWLDirectedGraphTest(boolean fair) {
        this.fair = fair;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {false}, {true}});
    }

    @Override
    protected <V> Graph<V> createGraph() {
        return new RWLDirectedGraph<>(fair);
    }
}
