package com.uranusdb.tests.graph;

import com.uranusdb.graph.FastUtilGraph;
import com.uranusdb.graph.Graph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;

public class RecommendationTest {

    private Graph db;
    private Random rand = new Random();

    int itemCount = 200;
    int personCount = 1000;
    int likesCount = 10;

    @Before
    public void setup() throws IOException {
        db = new FastUtilGraph();

        for (int item = 0; item < itemCount; item++) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("id", item);
            properties.put("itemname", "itemname" + item );
            db.addNode("Item", "item" + item, properties);
        }

        for (int person = 0; person < personCount; person++) {
            db.addNode("Person", "person" + person);
            for (int like = 0; like < likesCount; like++) {
                db.addRelationship("LIKES", "Person", "person" + person, "Item", "item" + rand.nextInt(itemCount));
            }
        }
    }

    @After
    public void tearDown() {
        db = null;
    }

    @Test
    public void shouldRecommend() {
        Collection<Integer> itemsYouLike = db.getOutgoingRelationshipNodeIds("LIKES", "Person", "person" + rand.nextInt(personCount));
        Map<Integer, LongAdder> occurrences = new HashMap<>();
        for (Integer item : itemsYouLike) {
            for (Integer person : db.getIncomingRelationshipNodeIds("LIKES", item)) {
                db.getOutgoingRelationshipNodeIds("LIKES", person)
                        .forEach(i-> occurrences.computeIfAbsent(i, (t) -> new LongAdder())
                                .increment());

            }
        }
        occurrences.remove(itemsYouLike);
        List<Map.Entry<Integer, LongAdder>> itemList = new ArrayList<>(occurrences.entrySet());
        Collections.sort(itemList, (a, b) -> ( b.getValue().intValue() - a.getValue().intValue() ));
        itemList.subList(0, Math.min(itemList.size(), 10));
    }
}
