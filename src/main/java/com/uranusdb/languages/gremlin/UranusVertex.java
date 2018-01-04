package com.uranusdb.languages.gremlin;

import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.*;

public class UranusVertex extends UranusElement implements Vertex {
    static final Set<String> META = new HashSet<String>() {{ add("_id"); add("_label"); add("_key");}};

    private String label;
    private String key;

    public UranusVertex(final Object id, final UranusGraph uranusGraph) {
        super(id, uranusGraph);
        Map<String, Object> raw_properties;
        try {
            raw_properties = uranusGraph.graph.getNodeById((int)this.id());
        } catch (Exception e) {
            throw new NoSuchElementException();
        }

        this.label = (String)raw_properties.get("_label");
        this.key = (String)raw_properties.get("_key");
    }

    @Override
    public String label() {
        return label;
    }

    public String key() {
        return key;
    }

    @Override
    public Graph graph() {
        return this.graph;
    }

    @Override
    public void remove() {
        graph.graph.removeNode(label, key);
    }

    @Override
    public Edge addEdge(String type, Vertex inVertex, Object... keyValues) {
        if (null == inVertex) throw Graph.Exceptions.argumentCanNotBeNull("vertex");
        ElementHelper.legalPropertyKeyValueArray(keyValues);
        ElementHelper.validateLabel(type);
        Object idValue = ElementHelper.getIdValue(keyValues).orElse(null);
        if (idValue != null) {
            throw Edge.Exceptions.userSuppliedIdsNotSupported();
        }
        Map<String, Object> properties = ElementHelper.asMap(keyValues);
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            ElementHelper.validateProperty(entry.getKey(), entry.getValue());
        }

        String to = graph.graph.getNodeKey((int)inVertex.id());
        int id = graph.graph.addRelationship(type, label, key, inVertex.label(), to, properties);
        return new UranusEdge(id, graph);
    }

    @Override
    public <V> VertexProperty<V> property(String key){
        Map<String, Object> properties = graph.graph.getNodeById((int)id);
        return properties.keySet().contains(key) ? new UranusVertexProperty(this, key, properties.get(key)) : VertexProperty.<V>empty();
    }

    @Override
    public <V> VertexProperty<V> property(VertexProperty.Cardinality cardinality, String property, V value, Object... keyValues) {
        ElementHelper.legalPropertyKeyValueArray(keyValues);
        ElementHelper.validateProperty(property, value);
        this.graph.graph.updateNodeProperty(label, key, property, value );
        return new UranusVertexProperty(this, property, value);
    }

    @Override
    public String toString() {
        return StringFactory.vertexString(this);
    }

    @Override
    public Iterator<Edge> edges(Direction direction, String... edgeLabels) {

        final List<Edge> edges = new ArrayList<>();

        if (direction.equals(Direction.OUT) || direction.equals(Direction.BOTH)) {
            if (edgeLabels.length == 0) {
                graph.graph.getOutgoingRelationshipIds((int) id).forEach(relId -> edges.add(new UranusEdge(relId, graph)));
            } else {
                for (String type : edgeLabels) {
                    graph.graph.getOutgoingRelationshipIds(type, (int) id).forEach(relId -> edges.add(new UranusEdge(relId, graph)));
                }
            }
        }
        if (direction.equals(Direction.IN) || direction.equals(Direction.BOTH)) {
            if (edgeLabels.length == 0) {
                graph.graph.getIncomingRelationshipIds((int)id).forEach( relId -> edges.add(new UranusEdge(relId, graph)));
            } else {
                for (String type : edgeLabels) {
                    graph.graph.getIncomingRelationshipIds(type, (int)id).forEach( relId -> edges.add(new UranusEdge(relId, graph)));
                }
            }
        }
        return  edges.iterator();
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
        ArrayList<Integer> nodeIds = new ArrayList<>();
        if (0 == edgeLabels.length) {
            if (direction != Direction.IN) {
                nodeIds.addAll(graph.graph.getOutgoingRelationshipNodeIds((int) id));
            }
            if (direction != Direction.OUT) {
                nodeIds.addAll(graph.graph.getIncomingRelationshipNodeIds((int) id));
            }
        } else {
            if (direction != Direction.IN) {
                for (String type : edgeLabels) {
                    nodeIds.addAll(graph.graph.getOutgoingRelationshipNodeIds(type, (int) id));
                }
            }
            if (direction != Direction.OUT) {
                for (String type : edgeLabels) {
                    nodeIds.addAll(graph.graph.getIncomingRelationshipNodeIds(type, (int) id));
                }
            }
        }

        return new Iterator<Vertex>() {

            final Iterator<Integer> nodeIdIterator = nodeIds.iterator();

            @Override
            public boolean hasNext() {
                return this.nodeIdIterator.hasNext();
            }

            @Override
            public Vertex next() {
                return new UranusVertex(this.nodeIdIterator.next(), graph);
            }
        };

    }

    @Override
    public <V> Iterator<VertexProperty<V>> properties(String... propertyKeys) {
        Map<String, Object> raw_properties = graph.graph.getNodeById((int)id);
        ArrayList<UranusVertexProperty> properties = new ArrayList<>();
        Set keys = new HashSet<>(Arrays.asList(propertyKeys));
        if (propertyKeys.length == 1) {
            Object value = raw_properties.get(propertyKeys[0]);
            return null == value ? Collections.emptyIterator() : IteratorUtils.of(new UranusVertexProperty(this, propertyKeys[0], value));
        } else {
            for (Map.Entry<String, Object> entry : raw_properties.entrySet()) {
                if (!entry.getKey().startsWith("_") && (keys.isEmpty() || keys.contains(entry.getKey()))) {
                    properties.add(new UranusVertexProperty(this, entry.getKey(), entry.getValue()));
                }
            }

            return (Iterator) IteratorUtils.stream(properties).iterator();
        }
    }

    @Override
    public Set<String> keys() {
        Set<String> keys = new HashSet<>();
        for (String key : graph.graph.getNodeById((int)id).keySet()) {
            if (!key.startsWith("_")) {
                keys.add(key);
            }
        }
        return keys;
    }

}
