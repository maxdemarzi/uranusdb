package com.uranusdb.languages.gremlin;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.util.NoSuchElementException;

public class UranusProperty<V> implements Property<V> {
    protected final Element element;
    protected final String key;
    protected V value;


    public UranusProperty(final Element element, final String key, final V value) {
        this.element = element;
        this.key = key;
        this.value = value;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public V value() throws NoSuchElementException {
        return value;
    }

    @Override
    public boolean isPresent() {
        return null != this.value;
    }

    @Override
    public Element element() {
        return this.element;
    }

    @Override
    public String toString() {
        return StringFactory.propertyString(this);
    }

    @Override
    public void remove() {
        if (this.element instanceof Edge) {
            ((UranusEdge) this.element).graph.graph.deleteRelationshipProperty((int)element.id(), key);
        } else {
            ((UranusVertex) this.element).graph.graph.deleteNodeProperty((int)element.id(), key);
        }
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object object) {
        return ElementHelper.areEqual(this, object);
    }
}
