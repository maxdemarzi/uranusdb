package com.uranusdb.languages.gremlin;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.GraphVariableHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class UranusGraphVariables implements Graph.Variables {

    private final Map<String, Object> variables = new HashMap<>();

    public UranusGraphVariables() {

    }

    @Override
    public Set<String> keys() {
        return this.variables.keySet();
    }

    @Override
    public <R> Optional<R> get(final String key) {
        return Optional.ofNullable((R) this.variables.get(key));
    }

    @Override
    public void remove(final String key) {
        this.variables.remove(key);
    }

    @Override
    public void set(final String key, final Object value) {
        GraphVariableHelper.validateVariable(key, value);
        this.variables.put(key, value);
    }

    public String toString() {
        return StringFactory.graphVariablesString(this);
    }
}
