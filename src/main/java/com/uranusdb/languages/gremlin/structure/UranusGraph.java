package com.uranusdb.languages.gremlin.structure;

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
@Graph.OptOut(
        test = "org.apache.tinkerpop.gremlin.structure.SerializationTest$GryoV3d0Test",
        method = "shouldSerializeTree",
        reason = "Not sure, to_s is equals, but one graph has UranusVertex vs DetachedVertex")
@Graph.OptOut(
        test = "org.apache.tinkerpop.gremlin.structure.SerializationTest$GryoV1d0Test",
        method = "shouldSerializeTree",
        reason = "Not sure, to_s is equals, but one graph has UranusVertex vs DetachedVertex")
@Graph.OptOut(
        test = "org.apache.tinkerpop.gremlin.structure.SerializationTest$GraphSONV1d0Test",
        method = "shouldSerializeTraversalMetrics",
        reason = "This test should only run for TinkerGraph")
@Graph.OptOut(
        test = "org.apache.tinkerpop.gremlin.process.traversal.step.map.ProfileTest$Traversals",
        method = "testProfileStrategyCallback",
        reason = "Tests calls for third entry but maybe should do by id 3.0.0()?")

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
        relationship.put("~id", id);
        return relationship;
    }

    public Map<String, Object> getNodeById(int id) {
        Map<String, Object> node = graph.getNodeById(id);
        node.put("~id", id);
        return node;
    }

    public Map<String, Object> getNode(String label, String key) {
        int id = graph.getNodeId(label, key);
        if (id == -1) { return null; }
        Map<String, Object> node = graph.getNodeById(id);
        node.put("~id", id);
        node.put("~label", label);
        node.put("~key", key);
        return node;
    }

    public static UranusGraph open(final Configuration configuration) {
        return new UranusGraph(configuration);
    }

    @Override
    public Vertex addVertex(Object... keyValues) {
        ElementHelper.legalPropertyKeyValueArray(keyValues);
        final String label = ElementHelper.getLabelValue(keyValues).orElse(Vertex.DEFAULT_LABEL);
        Object key = UranusHelper.getOptionalValue("key", keyValues).orElse(null);
        if (key == null) {
            key = UUID.randomUUID().toString();
        }

        Object nodeId = ElementHelper.getIdValue(keyValues).orElse(null);
        if (nodeId != null) { throw VertexProperty.Exceptions.userSuppliedIdsNotSupported(); }

        Map<String, Object> properties =  new HashMap<>();
        for (int i = 0; i < keyValues.length; i = i + 2) {
            if (!keyValues[i].equals(T.label))
                properties.put((String)keyValues[i], keyValues[i+1]);
        }
        // Extra validation
        if (properties.keySet().contains("")) { throw Property.Exceptions.propertyKeyCanNotBeEmpty(); }

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
            return IteratorUtils.stream(
                    graph.getAllNodeIds())
                    .map(id -> new UranusVertex(id, this))
                    .iterator();
        } else {
            final List<Object> idList = Arrays.asList(vertexIds);
            validateHomogenousIds(idList);

            return IteratorUtils.stream(idList).map(node -> (Vertex) new UranusVertex(node, this))
                    .filter(vertex -> ((UranusVertex)vertex).exists())
                    .iterator();
        }
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {

        if (edgeIds.length == 0) {
            //return IteratorUtils.stream(graph.getAllRelationships()).map(relationship -> (Edge) new UranusEdge((int)relationship.get("~id"), this)).iterator();
            return IteratorUtils.stream(graph.getAllRelationshipIds()).map(id -> new UranusEdge(id, this))
                    //.filter(Objects::nonNull)
                    .iterator();
        } else {

            final List<Object> idList = Arrays.asList(edgeIds);
            validateHomogenousIds(idList);

            return IteratorUtils.stream(idList).map(rel -> (Edge) new UranusEdge(rel, this))
                    //.filter(edge -> ((UranusEdge)edge).exists())
                    .filter(Objects::nonNull)
                    .iterator();
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

        @Override
        public String toString() {
            return StringFactory.featureString(this);
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
            return true;
        }

        @Override
        public boolean supportsSerializableValues() {
            return true;
        }

        @Override
        public boolean supportsUniformListValues() {
            return true;
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
            return true;
        }

        @Override
        public boolean supportsSerializableValues() {
            return true;
        }

        @Override
        public boolean supportsUniformListValues() {
            return true;
        }

    }
}
