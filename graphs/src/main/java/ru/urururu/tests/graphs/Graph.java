package ru.urururu.tests.graphs;

import java.util.List;
import java.util.Optional;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 * 
 * @param <V> the type of vertices in this graph
 */
public interface Graph<V> {
    
     void addVertex(V vertex);

     void addEdge(V from, V to);

     Optional<List<Edge<V>>> getPath(V from, V to);
}
