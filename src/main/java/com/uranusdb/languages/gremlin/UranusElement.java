package com.uranusdb.languages.gremlin;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;

public abstract class UranusElement implements Element {

    protected final Object id;
    protected final UranusGraph graph;

    public UranusElement(final Object id, final UranusGraph graph) {
        if (id instanceof Integer) {
            this.id = id;
        } else if (id instanceof Number) {
            this.id =  ((Number) id).intValue();
        } else if(id instanceof String) {
            this.id = Integer.parseInt((String)id);
        } else {
            this.id = ((Element)id).id();
        }
        this.graph = graph;
    }

    @Override
    public Object id() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return ElementHelper.hashCode(this);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object object) {
        return ElementHelper.areEqual(this, object);
    }
}
