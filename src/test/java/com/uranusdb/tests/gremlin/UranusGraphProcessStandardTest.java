package com.uranusdb.tests.gremlin;

import com.uranusdb.languages.gremlin.UranusGraph;
import org.apache.tinkerpop.gremlin.GraphProviderClass;
import org.apache.tinkerpop.gremlin.process.ProcessStandardSuite;
import org.junit.runner.RunWith;

@RunWith(ProcessStandardSuite.class)
@GraphProviderClass(provider = UranusGraphProvider.class, graph = UranusGraph.class)
public class UranusGraphProcessStandardTest {
}
