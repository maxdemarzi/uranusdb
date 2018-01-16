package com.uranusdb.tests.benchmarks;

import com.uranusdb.graph.FastUtilGraph;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@State(Scope.Benchmark)
public class AggregationBenchmark {
    private FastUtilGraph db;
    private Random rand = new Random();

    @Param({"100", "1632803"})
    private int personCount;

    @Setup(Level.Iteration)
    public void prepare() throws IOException {
        db = new FastUtilGraph();

        for (int personId = 0; personId < personCount; personId++) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("id", personId);
            properties.put("age", rand.nextInt(120));
            db.addNode("Person", "person" + personId, properties);
        }
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void measureAggregation() throws IOException {
        Iterator<Map<String, Object>> iterator = db.getAllNodes();
        HashMap<Integer, LongAdder> ages = new HashMap<>();
        iterator.forEachRemaining(p -> ages.computeIfAbsent((Integer)p.get("age"), (t) -> new LongAdder())
                .increment());
    }
}
