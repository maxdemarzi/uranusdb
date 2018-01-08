package com.uranusdb.tests.gremlin;

import com.uranusdb.languages.gremlin.structure.UranusGraph;
import org.apache.tinkerpop.gremlin.GraphProviderClass;
import org.apache.tinkerpop.gremlin.structure.StructureStandardSuite;
import org.junit.runner.RunWith;

@RunWith(StructureStandardSuite.class)
@GraphProviderClass(provider = UranusGraphProvider.class, graph = UranusGraph.class)
public class UranusGraphStructureStandardTest {
}
