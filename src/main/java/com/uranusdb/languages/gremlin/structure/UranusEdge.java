package com.uranusdb.languages.gremlin.structure;

import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.*;

public class UranusEdge extends UranusElement implements Edge {
    private int outgoing_node_id;
    private int incoming_node_id;
    private String type;
    private boolean exists = false;

    public UranusEdge(final Object id, UranusGraph graph) {
        super(id, graph);

        Map<String, Object> raw_properties = null;
        try {
            raw_properties = graph.getRelationshipById((int)this.id());
        } catch (Exception e) {
            throw new NoSuchElementException();
        }

        if (raw_properties != null) {
            this.outgoing_node_id = (int) raw_properties.get("~outgoing_node_id");
            this.incoming_node_id = (int) raw_properties.get("~incoming_node_id");
            this.type = (String) raw_properties.get("~type");
            this.exists = true;
        }
    }

    public boolean exists() {
        return this.exists;
    }

    @Override
    public Vertex outVertex() {
        return new UranusVertex(incoming_node_id, this.graph);
    }

    @Override
    public Vertex inVertex() {
        return new UranusVertex(outgoing_node_id, this.graph);
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction) {
        switch (direction) {
            case OUT:
                return IteratorUtils.of(this.outVertex());
            case IN:
                return IteratorUtils.of(this.inVertex());
            default:
                return IteratorUtils.of(this.outVertex(), this.inVertex());
        }
    }

    @Override
    public String label() {
        return type;
    }

    @Override
    public Graph graph() {
        return this.graph;
    }

    @Override
    public <V> Property<V> property(String key){
        Map<String, Object> properties = graph.graph.getRelationshipById((int)id);
        return properties.keySet().contains(key) ? new UranusProperty(this, key, properties.get(key)) : Property.<V>empty();
    }

    @Override
    public <V> Property<V> property(String key, V value) {
        ElementHelper.validateProperty(key, value);
        graph.graph.updateRelationshipProperty((int)id, key, value);
        return new UranusProperty<>(this, key, value);
    }

    @Override
    public void remove() {
        graph.graph.removeRelationship((int)id);
    }

    @Override
    public <V> Iterator<Property<V>> properties(String... propertyKeys) {
        Map<String, Object> raw_properties = graph.graph.getRelationshipById((int)id);
        ArrayList<UranusProperty> properties = new ArrayList<>();
        if (propertyKeys.length == 1) {
            Object value = raw_properties.get(propertyKeys[0]);
            return null == value ? Collections.emptyIterator() : IteratorUtils.of(new UranusProperty(this, propertyKeys[0], value));
        } else {
            for (Map.Entry<String, Object> entry : raw_properties.entrySet()) {
                if (!entry.getKey().startsWith("~")) {
                    properties.add(new UranusProperty(this, entry.getKey(), entry.getValue()));
                }
            }
            return (Iterator) IteratorUtils.stream(properties).iterator();
        }
    }

    @Override
    public Set<String> keys() {
        Set<String> keys = new HashSet<>();
        for (String key : graph.graph.getRelationshipById((int)id).keySet()) {
            if (!key.startsWith("~")) {
                keys.add(key);
            }
        }
        return keys;
    }

    @Override
    public String toString() {
        return StringFactory.edgeString(this);
    }
}
