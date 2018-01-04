package com.uranusdb.languages.gremlin;

import com.uranusdb.graph.FastUtilGraph;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.*;

@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_STANDARD)
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_INTEGRATE)
@Graph.OptIn(Graph.OptIn.SUITE_PROCESS_STANDARD)
//@Graph.OptIn(Graph.OptIn.SUITE_PROCESS_COMPUTER)
public class UranusGraph implements Graph {

    private static final Configuration EMPTY_CONFIGURATION = new BaseConfiguration() {{
        this.setProperty(Graph.GRAPH, UranusGraph.class.getName());
    }};

    private final UranusGraphFeatures features = new UranusGraphFeatures();
    private final Configuration configuration;
    protected final com.uranusdb.graph.Graph graph = new FastUtilGraph();

    protected UranusGraphVariables variables = null;

    private UranusGraph(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        return StringFactory.graphString(this, "vertices:" + IteratorUtils.count(this.vertices()) + " edges:" + IteratorUtils.count(this.edges()));
    }

    // Helpers to hydrate Nodes and Relationships
    public Map<String, Object> getRelationshipById(int id) {
        Map<String, Object> relationship = graph.getRelationshipById(id);
        relationship.put("_id", id);
        return relationship;
    }

    public Map<String, Object> getNodeById(int id) {
        Map<String, Object> node = graph.getNodeById(id);
        node.put("_id", id);
        return node;
    }

    public Map<String, Object> getNode(String label, String key) {
        int id = graph.getNodeId(label, key);
        if (id == -1) { return null; }
        Map<String, Object> node = graph.getNodeById(id);
        node.put("_id", id);
        node.put("_label", label);
        node.put("_key", key);
        return node;
    }

    public static UranusGraph open(final Configuration configuration) {
        return new UranusGraph(configuration);
    }

    @Override
    public Vertex addVertex(Object... keyValues) {
        ElementHelper.legalPropertyKeyValueArray(keyValues);
        final String label = ElementHelper.getLabelValue(keyValues).orElse(Vertex.DEFAULT_LABEL);
        Object key = ElementHelper.getIdValue(keyValues).orElse(null);
        if (key == null) {
            key = UUID.randomUUID().toString();
        }

        Map<String, Object> properties;
        try {
            properties = ElementHelper.asMap(keyValues);
        } catch (IllegalStateException ex) {
            throw VertexProperty.Exceptions.multiPropertiesNotSupported();
        }
        properties.remove("label");
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            ElementHelper.validateProperty(entry.getKey(), entry.getValue());
        }

        int id = graph.addNode(label, (String)key, properties);
        return new UranusVertex(id, this);
    }

    @Override
    public <C extends GraphComputer> C compute(Class<C> graphComputerClass) throws IllegalArgumentException {
        throw Graph.Exceptions.graphDoesNotSupportProvidedGraphComputer(graphComputerClass);
    }

    @Override
    public GraphComputer compute() throws IllegalArgumentException {
        throw Graph.Exceptions.graphComputerNotSupported();
    }

    @Override
    public Iterator<Vertex> vertices(Object... vertexIds) {
        if (vertexIds.length == 0) {
            return IteratorUtils.stream(graph.getAllNodes()).map(node -> (Vertex) new UranusVertex(node.get("_id"), this)).iterator();
        } else {
            final List<Object> idList = Arrays.asList(vertexIds);
            validateHomogenousIds(idList);

            return IteratorUtils.stream(idList).map(node -> (Vertex) new UranusVertex(node, this)).iterator();
        }
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {

        if (edgeIds.length == 0) {
            return IteratorUtils.stream(graph.getAllRelationships()).map(relationship -> (Edge) new UranusEdge((int)relationship.get("_id"), this)).iterator();
        } else {

            final List<Object> idList = Arrays.asList(edgeIds);
            validateHomogenousIds(idList);

            return IteratorUtils.stream(idList).map(rel -> (Edge) new UranusEdge(rel, this)).iterator();

//            return Stream.of(edgeIds)
//                    .map(id -> {
//                        if (id instanceof Number)
//                            return ((Number) id).intValue();
//                        else if (id instanceof String)
//                            return Integer.valueOf(id.toString());
//                        else if (id instanceof Edge) {
//                            return (int) ((Edge) id).id();
//                        } else
//                            throw new IllegalArgumentException("Unknown edge id type: " + id);
//                    })
//                    .map(id -> (Edge) new UranusEdge(id, this)).iterator();
        }
    }

    @Override
    public Transaction tx() {
        throw Exceptions.transactionsNotSupported();
    }

    @Override
    public void close() throws Exception {
        graph.clear();
    }

    @Override
    public Variables variables() {
        if (null == this.variables)
            this.variables = new UranusGraphVariables();
        return this.variables;
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public Features features() {
        return features;
    }

    private void validateHomogenousIds(final List<Object> ids) {
        final Iterator<Object> iterator = ids.iterator();
        Object id = iterator.next();
        if (id == null)
            throw Graph.Exceptions.idArgsMustBeEitherIdOrElement();
        final Class firstClass = id.getClass();
        while (iterator.hasNext()) {
            id = iterator.next();
            if (id == null || !id.getClass().equals(firstClass))
                throw Graph.Exceptions.idArgsMustBeEitherIdOrElement();
        }
    }

    public class UranusGraphFeatures implements Features {
        private final UranusGraphGraphFeatures graphFeatures = new UranusGraphGraphFeatures();
        private final UranusGraphEdgeFeatures edgeFeatures = new UranusGraphEdgeFeatures();
        private final UranusGraphVertexFeatures vertexFeatures = new UranusGraphVertexFeatures();

        private UranusGraphFeatures() {
        }

        @Override
        public GraphFeatures graph() {
            return graphFeatures;
        }

        @Override
        public EdgeFeatures edge() {
            return edgeFeatures;
        }

        @Override
        public VertexFeatures vertex() {
            return vertexFeatures;
        }

    }

    public class UranusGraphVertexFeatures implements Features.VertexFeatures {

        private final UranusGraphVertexPropertyFeatures vertexPropertyFeatures = new UranusGraphVertexPropertyFeatures();

        private UranusGraphVertexFeatures() {
        }

        @Override
        public Features.VertexPropertyFeatures properties() {
            return vertexPropertyFeatures;
        }

        @Override
        public boolean supportsCustomIds() {
            return false;
        }

        @Override
        public boolean supportsUserSuppliedIds() {
            return false;
        }

        @Override
        public boolean supportsAnyIds() {
            return false;
        }

//        @Override
//        public boolean willAllowId(final Object id) {
//            if (id instanceof String) {
//                int split = ((String) id).indexOf("-");
//                if (split > 0 && split < ((String) id).length()) {
//                    return true;
//                }
//            } else  if (id instanceof Map) {
//                Map map = (Map) id;
//                if (map.containsKey("label") && map.containsKey("id")) {
//                    return true;
//                }
//            }
//
//            return false;
//        }

        @Override
        public VertexProperty.Cardinality getCardinality(final String key) {
            return VertexProperty.Cardinality.list;
        }

        @Override
        public boolean supportsMetaProperties() {
            return false;
        }

        @Override
        public boolean supportsMultiProperties() {
            return false;
        }

    }

    public class UranusGraphEdgeFeatures implements Features.EdgeFeatures {

        private final Features.EdgePropertyFeatures edgePropertyFeatures = new UranusEdgePropertyFeatures();

        private UranusGraphEdgeFeatures() {
        }

        @Override
        public Features.EdgePropertyFeatures properties() {
            return edgePropertyFeatures;
        }

        @Override
        public boolean supportsCustomIds() {
            return false;
        }

        @Override
        public boolean supportsUserSuppliedIds() {
            return false;
        }

        @Override
        public boolean supportsAnyIds() {
            return false;
        }

    }

    public class UranusGraphGraphFeatures implements Features.GraphFeatures {

        private UranusGraphGraphFeatures() {
        }

        @Override
        public boolean supportsComputer() {
            return false;
        }

        @Override
        public boolean supportsPersistence() {
            return false;
        }

        @Override
        public boolean supportsConcurrentAccess() {
            return false;
        }

        @Override
        public boolean supportsTransactions() {
            return false;
        }

        @Override
        public boolean supportsThreadedTransactions() {
            return false;
        }

    }

    public class UranusGraphVertexPropertyFeatures implements Features.VertexPropertyFeatures {

        private UranusGraphVertexPropertyFeatures() {
        }

        @Override
        public boolean supportsMapValues() {
            return true;
        }

        @Override
        public boolean supportsMixedListValues() {
            return false;
        }

        @Override
        public boolean supportsSerializableValues() {
            return false;
        }

        @Override
        public boolean supportsUniformListValues() {
            return false;
        }

        @Override
        public boolean supportsUserSuppliedIds() {
            return false;
        }

        @Override
        public boolean supportsAnyIds() {
            return false;
        }

        @Override
        public boolean supportsCustomIds() {
            return false;
        }
    }

    public class UranusEdgePropertyFeatures implements Features.EdgePropertyFeatures {

        UranusEdgePropertyFeatures() {
        }

        @Override
        public boolean supportsMapValues() {
            return true;
        }

        @Override
        public boolean supportsMixedListValues() {
            return false;
        }

        @Override
        public boolean supportsSerializableValues() {
            return false;
        }

        @Override
        public boolean supportsUniformListValues() {
            return false;
        }

    }
}
