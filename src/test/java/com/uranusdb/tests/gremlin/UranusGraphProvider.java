package com.uranusdb.tests.gremlin;

import com.uranusdb.languages.gremlin.structure.*;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.AbstractGraphProvider;
import org.apache.tinkerpop.gremlin.LoadGraphWith;
import org.apache.tinkerpop.gremlin.structure.Graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
// See https://github.com/apache/tinkerpop/blob/master/tinkergraph-gremlin/src/test/java/org/apache/tinkerpop/gremlin/tinkergraph/TinkerGraphProvider.java
public class UranusGraphProvider extends AbstractGraphProvider {

    private static final Set<Class> IMPLEMENTATION = new HashSet<Class>() {{
        add(UranusEdge.class);
        add(UranusElement.class);
        add(UranusGraph.class);
        add(UranusGraphVariables.class);
        add(UranusProperty.class);
        add(UranusVertex.class);
        add(UranusVertexProperty.class);
    }};

    @Override
    public Map<String, Object> getBaseConfiguration(String graphName, Class<?> test, String testMethodName, LoadGraphWith.GraphData loadGraphWith) {
        return new HashMap<String, Object>() {{
                put(Graph.GRAPH, UranusGraph.class.getName());
            }};
    }

    @Override
    public void clear(Graph graph, Configuration configuration) throws Exception {
        if (graph != null) {
            graph.close();
        }
    }

    @Override
    public Set<Class> getImplementations() {
        return IMPLEMENTATION;
    }
}
