package com.uranusdb.tests.benchmarks;

import com.uranusdb.graph.FastUtilGraph;
import org.junit.Ignore;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class GraphWriteBenchmarks {

    private FastUtilGraph db;
    private Random rand = new Random();

    @Param({"1000"})
    private int userCount;

    @Param({"100"})
    private int personCount;

    @Param({"20000"})
    private int itemCount;

    @Param({"100"})
    private int friendsCount;

    @Param({"100"})
    private int likesCount;

    @Setup(Level.Iteration)
    public void prepare() throws IOException {
        db = new FastUtilGraph();

        for (int item = 0; item < itemCount; item++) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("id", item);
            properties.put("itemname", "itemname" + item );
            db.addNode("Item","item" + item, properties);
        }

        for (int person = 0; person < personCount; person++) {
            db.addNode("Person","person" + person);
            for (int like = 0; like < likesCount; like++) {
                db.addRelationship("LIKES", "Person", "person" + person, "Item","item" + rand.nextInt(itemCount));
            }
        }
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureCreateEmptyNode() throws IOException {
        db.addNode("User","user" + rand.nextLong());
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Ignore
    public int measureCreateEmptyNodes() throws IOException {
        int user;
        for (user = 0; user < userCount; user++) {
            db.addNode("User","user" + user);
        }
        db.clear();
        return user;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public int measureCreateNodesWithProperties() throws IOException {
        int user;
        for (user = 0; user < userCount; user++) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("username", "username" + rand.nextInt() );
            properties.put("age", + rand.nextInt(100) );
            properties.put("weight", rand.nextInt(300) );
            db.addNode("User", String.valueOf(rand.nextInt()), properties);
        }
        return user;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureCreateNodeWithProperties() throws IOException {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("username", "username" + rand.nextInt() );
        properties.put("age", + rand.nextInt(100) );
        properties.put("weight", rand.nextInt(300) );
        db.addNode( "User",String.valueOf(rand.nextInt()), properties);
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public int measureCreateEmptyNodesAndRelationships() throws IOException {
        int user;
        for (user = 0; user < userCount; user++) {
            db.addNode("User","user" + user);
        }
        for (user = 0; user < userCount; user++) {
            for (int like = 0; like < friendsCount; like++) {
                db.addRelationship("FRIENDS", "User", "user" + user,"User", "user" + rand.nextInt(userCount));
            }
        }
        return user;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public int measureCreateRelationships() throws IOException {
        int count = 0;
        for (int person = 0; person < personCount; person++) {
            for (int like = 0; like < friendsCount; like++) {
                db.addRelationship("FRIENDS", "Person", "person" + person,"Person", "person" + rand.nextInt(personCount));
                count++;
            }
        }
        return count;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureCreateRandomRelationship() throws IOException {
        db.addRelationship("LIKES", "Person", "person" + rand.nextInt(personCount), "Person","person" + rand.nextInt(personCount));
    }

}
