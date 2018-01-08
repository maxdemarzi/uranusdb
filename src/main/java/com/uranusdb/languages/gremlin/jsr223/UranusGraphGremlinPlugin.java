package com.uranusdb.languages.gremlin.jsr223;

import com.uranusdb.languages.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.jsr223.AbstractGremlinPlugin;
import org.apache.tinkerpop.gremlin.jsr223.DefaultImportCustomizer;
import org.apache.tinkerpop.gremlin.jsr223.ImportCustomizer;

public class UranusGraphGremlinPlugin extends AbstractGremlinPlugin {
    private static final String NAME = "tinkerpop.uranusgraph";

    private static final ImportCustomizer imports = DefaultImportCustomizer.build()
            .addClassImports(UranusEdge.class,
                    UranusElement.class,
                    UranusGraph.class,
                    UranusGraphVariables.class,
                    UranusHelper.class,
                    UranusProperty.class,
                    UranusVertex.class,
                    UranusVertexProperty.class).create();

    private static final UranusGraphGremlinPlugin instance = new UranusGraphGremlinPlugin();

    public UranusGraphGremlinPlugin() {
        super(NAME, imports);
    }

    public static UranusGraphGremlinPlugin instance() {
        return instance;
    }
}
