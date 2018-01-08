package com.uranusdb.languages.gremlin.structure;

import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class UranusVertexProperty<V> implements VertexProperty<V> {
    protected final UranusVertex vertex;
    protected final String key;
    protected final V value;

    public UranusVertexProperty(final UranusVertex vertex, final String key, final V value) {
        this.vertex = vertex;
        this.key = key;
        this.value = value;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public V value() throws NoSuchElementException {
        return this.value;
    }

    @Override
    public boolean isPresent() {
        return null != this.value;
    }

    @Override
    public Vertex element() {
        return this.vertex;
    }

    @Override
    public void remove() {
        this.vertex.graph.graph.deleteNodeProperty((int)vertex.id, key);
    }

    @Override
    public Object id() {
        return (long) (this.key.hashCode() + this.value.hashCode() + this.vertex.id().hashCode());
    }

    @Override
    public <V> Property<V> property(String key, V value) {
        final Property<V> property = new UranusProperty(this, key, value);
        return property;
    }

    @Override
    public <V> Iterator<Property<V>> properties(String... propertyKeys) {
        final Property<V> property = new UranusProperty(this, key, value);
        return IteratorUtils.of(property);
    }

    @Override
    public String toString() {
        return StringFactory.propertyString(this);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object object) {
        return ElementHelper.areEqual(this, object);
    }
}
