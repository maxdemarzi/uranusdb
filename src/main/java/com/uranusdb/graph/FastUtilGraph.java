package com.uranusdb.graph;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import org.roaringbitmap.RoaringBitmap;

import java.util.*;

public class FastUtilGraph implements Graph {
    private Object2ObjectArrayMap<String, Object2IntOpenHashMap<String>> nodeKeys;
    private ObjectArrayList<Map<String, Object>> nodes;
    private Object2ObjectArrayMap<String, Long2IntOpenHashMap> relationshipKeys;
    private ObjectArrayList<Map<String, Object>> relationships;
    private Object2ObjectOpenHashMap<String, ReversibleMultiMap> related;
    private Object2IntArrayMap<String> relationshipCounts;
    private Object2ObjectArrayMap<String, Long2IntOpenHashMap> relatedCounts;
    private RoaringBitmap deletedNodes;
    private RoaringBitmap deletedRelationships;

    public FastUtilGraph() {
        nodeKeys = new Object2ObjectArrayMap<>();
        nodes = new ObjectArrayList<>();
        relationshipKeys = new Object2ObjectArrayMap<>();
        relationships = new ObjectArrayList<>();
        related = new Object2ObjectOpenHashMap<>();
        relationshipCounts = new Object2IntArrayMap<>();
        relationshipCounts.defaultReturnValue(0);
        relatedCounts = new Object2ObjectArrayMap<>();
        deletedNodes = new RoaringBitmap();
        deletedRelationships = new RoaringBitmap();
    }

    public void clear() {
        nodeKeys.clear();
        nodes.clear();
        relationships.clear();
        related.clear();
        relationshipCounts.clear();
        relatedCounts.clear();
        deletedNodes.clear();
        deletedRelationships.clear();
    }

    // Relationship Types
    public Set<String> getRelationshipTypes() {
        return related.keySet();
    }

    public Map<String, Integer> getRelationshipTypesCount() {
        return relationshipCounts;
    }

    public Integer getRelationshipTypeCount(String type) {
        return relationshipCounts.getInt(type);
    }

    private Object2IntOpenHashMap<String> getOrCreateNodeKey(String label) {
        Object2IntOpenHashMap<String> nodeKey;

        if (!nodeKeys.containsKey(label)) {
            nodeKey = new Object2IntOpenHashMap<>();
            nodeKey.defaultReturnValue(-1);
            nodeKeys.put(label, nodeKey);
        } else {
            nodeKey = nodeKeys.get(label);
        }
        return nodeKey;
    }

    private int getNodeKeyId(String label, String id) {
        if (!nodeKeys.containsKey(label)) {
            return -1;
        } else {
            return nodeKeys.get(label).getInt(id);
        }
    }

    private void removeNodeKeyId(String label, String id) {
        nodeKeys.get(label).removeInt(id);
    }

    private void addRelationshipKeyId(String type, int count, int node1, int node2, int id ) {

        if (!relationshipKeys.containsKey(type + count)) {
            Long2IntOpenHashMap relKey = new Long2IntOpenHashMap();
            relKey.defaultReturnValue(-1);
            relKey.put(((long)node1 << 32) + node2, id);
            relationshipKeys.put(type + count, relKey);
        } else {
            relationshipKeys.get(type + count).put(((long)node1 << 32) + node2, id);
        }

    }
    private int getRelationshipKeyId(String type, int count, int node1, int node2) {

        if (!relationshipKeys.containsKey(type + count)) {
            return -1;
        } else {
            return relationshipKeys.get(type + count).get(((long)node1 << 32) + node2);
        }
    }

    private void removeRelationshipKeyId(String type, int count, int node1, int node2) {
        relationshipKeys.get(type + count).remove(((long)node1 << 32) + node2);
    }

    // Nodes
    public int addNode (String label, String key) {
        return addNode(label, key, new HashMap<>());
    }

    public int addNode (String label, String key, Map<String, Object> properties) {
        Object2IntOpenHashMap<String> nodeKey = getOrCreateNodeKey(label);

        if (nodeKey.containsKey(key)) {
            return -1;
        } else {
            properties.put("_label", label);
            properties.put("_key", key);
            int nodeId;
            if (deletedNodes.isEmpty()) {
                nodeId = nodes.size();
                properties.put("_id", nodeId);
                nodes.add(properties);
                nodeKey.put(key, nodeId);
            } else {
                nodeId = deletedNodes.first();
                properties.put("_id", nodeId);
                nodes.set(nodeId, properties);
                nodeKey.put(key, nodeId);
                deletedNodes.remove(nodeId);
            }

            return nodeId;
        }
    }

    public boolean removeNode(String label, String key) {
        int id = getNodeKeyId(label, key);
        if (id == -1) { return false; }
        nodes.set(id, null);
        deletedNodes.add(id);

        for (String type : related.keySet()) {
            ReversibleMultiMap rels = related.get(type);
            int outgoingCount = 0;
            int incomingCount = 0;
            for (Integer value : rels.getRels(id)) {
                outgoingCount++;
                relationships.set(value, null);
                deletedRelationships.add(value);
            }
            for (Integer value : rels.getRelsByValue(id)) {
                incomingCount++;
                relationships.set(value, null);
                deletedRelationships.add(value);
            }
            rels.removeAll(id);
            relationshipCounts.put(type, relationshipCounts.getInt(type) - (outgoingCount + incomingCount));
        }
        removeNodeKeyId(label, key);

        return true;
    }

    public Map<String, Object> getNodeById(int id) {
        return nodes.get(id);
    }

    public Map<String, Object> getNode(String label, String key) {
        int id = getNodeKeyId(label, key);
        if (id == -1) { return null; }
        return nodes.get(id);
    }

    public int getNodeId(String label, String key) {
        return getNodeKeyId(label, key);
    }

    public String getNodeLabel(int id) {
        return (String)nodes.get(id).get("_label");
    }

    public String getNodeKey(int id) {
        return (String)nodes.get(id).get("_key");
    }

    // Node Properties
    public Object getNodeProperty(String label, String key, String property) {
        return getNodeProperty(getNodeKeyId(label, key), property);
    }

    public Object getNodeProperty(int id, String property) {
        if (id == -1) { return null; }
        return nodes.get(id).get(property);
    }

    public boolean updateNodeProperties(String label, String key, Map<String, Object> properties) {
        return updateNodeProperties(getNodeKeyId(label, key), properties);
    }

    public boolean updateNodeProperties(int id, Map<String, Object> properties) {
        if (id == -1) { return false; }
        if (properties.containsKey("_label") || properties.containsKey("_key") || properties.containsKey("_id")) {
            return false;
        }
        Map<String, Object> current = nodes.get(id);
        current.putAll(properties);
        return true;
    }

    public boolean deleteNodeProperties(String label, String key) {
        return deleteNodeProperties(getNodeKeyId(label, key));
    }

    public boolean deleteNodeProperties(int id) {
        if (id == -1) { return false; }
        Map<String, Object> current = nodes.get(id);
        Map<String, Object> properties = new HashMap<>();
        current.put("_id", id);
        current.put("_label", current.get("_label"));
        current.put("_key", current.get("_key"));
        nodes.add(id, properties);
        return true;
    }

    public boolean updateNodeProperty(String label, String key, String property, Object value) {
        return updateNodeProperty(getNodeKeyId(label, key), property, value);
    }

    public boolean updateNodeProperty(int id, String property, Object value) {
        if (id == -1) { return false; }
        Map<String, Object> properties = nodes.get(id);
        if (property.startsWith("_")) {
            return false;
        } else {
            properties.put(property, value);
        }
        return true;
    }

    public boolean deleteNodeProperty(String label, String key, String property) {
        return deleteNodeProperty(getNodeKeyId(label, key), property);
    }

    public boolean deleteNodeProperty(int id, String property) {
        if (id == -1) { return false; }
        Map<String, Object> properties = nodes.get(id);
        if (property.startsWith("_")) {
            return false;
        } else {
            properties.remove(property);
            return true;
        }
    }

    // Relationships
    public int addRelationship(String type, String label1, String from, String label2, String to) {
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return -1; }

        related.putIfAbsent(type, new ReversibleMultiMap());
        relationshipCounts.putIfAbsent(type, 0);
        relationshipCounts.put(type, relationshipCounts.getInt(type) + 1);

        relatedCounts.putIfAbsent(type, new Long2IntOpenHashMap());
        Long2IntOpenHashMap relatedCount = relatedCounts.get(type);
        long countId = ((long)node1 << 32) + node2;
        int count = relatedCount.get(countId) + 1;
        int id = relationships.size();
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("_incoming_node_id", node1);
        properties.put("_outgoing_node_id", node2);
        properties.put("_type", type);
        properties.put("_id", id);

        relationships.add(properties);
        relatedCount.put(countId, count);
        related.get(type).put(node1, node2, id);
        addRelationshipKeyId(type, count, node1, node2, id);

        return id;
    }

    public int addRelationship(String type, String label1, String from, String label2, String to, Map<String, Object> properties) {
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return -1; }

        related.putIfAbsent(type, new ReversibleMultiMap());
        relationshipCounts.putIfAbsent(type, 0);
        relationshipCounts.put(type, relationshipCounts.getInt(type) + 1);

        int id = relationships.size();
        properties.put("_incoming_node_id", node1);
        properties.put("_outgoing_node_id", node2);
        properties.put("_type", type);
        properties.put("_id", id);

        relatedCounts.putIfAbsent(type, new Long2IntOpenHashMap());
        Long2IntOpenHashMap relatedCount = relatedCounts.get(type);
        long countId = ((long)node1 << 32) + node2;
        int count = relatedCount.get(countId) + 1;
        // If this is the second or greater relationship of this type between these two nodes, add it to the properties, else assume it is one.
        if (count > 1) {
            properties.put("_count", count);
        }

        relationships.add(properties);
        relatedCount.put(countId, count);
        related.get(type).put(node1, node2, id);
        addRelationshipKeyId(type, count, node1, node2, id);

        return id;
    }

    public boolean removeRelationship(int id) {
        Map<String, Object> relationship = relationships.get(id);
        String type = (String)relationship.get("_type");
        int node1 = (int)relationship.get("_incoming_node_id");
        int node2 = (int)relationship.get("_outgoing_node_id");
        int count = (int)relationship.getOrDefault("_count", 1);
        related.get(type).removeRelationship(node1, node2, id);
        relationships.set(id, null);
        removeRelationshipKeyId(type, count, node1, node2);

        return true;
    }

    public boolean removeRelationship(String type, String label1, String from, String label2, String to) {
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return false; }

        if(!related.containsKey(type)) {
            return false;
        }
        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts.get(type).get(countId);
        if (count == 0) {
            return false;
        }
        relatedCounts.get(type).put(countId, count - 1);
        relationshipCounts.put(type, relationshipCounts.getInt(type) - 1);

        int relId = getRelationshipKeyId(type, count, node1, node2);

        related.get(type).removeRelationship(node1, node2, relId);
        relationships.set(relId, null);
        removeRelationshipKeyId(type, count, node1, node2);

        return true;
    }

    public boolean removeRelationship(String type, String label1, String from, String label2, String to, int number) {
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return false; }

        if(!related.containsKey(type)) {
            return false;
        }
        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts.get(type).get(countId);
        if (count == 0 || count < number) {
            return false;
        }
        relatedCounts.get(type).put(countId, count - 1);
        relationshipCounts.put(type, relationshipCounts.getInt(type) - 1);

        int relId = getRelationshipKeyId(type, number, node1, node2);

        related.get(type).removeRelationship(node1, node2, relId);
        relationships.set(relId, null);
        if (count == 1) {
            removeRelationshipKeyId(type, number, node1, node2);
        } else {
            if (count != number) {
                int movedRelId = getRelationshipKeyId(type, count, node1, node2);
                if (number > 1) {
                    relationships.get(movedRelId).put("_count", number);
                } else {
                    relationships.get(movedRelId).remove("_count");
                }
                addRelationshipKeyId(type, number, node1, node2, movedRelId);
            }
            removeRelationshipKeyId(type, count, node1, node2);
        }

        return true;
    }

    public Map<String, Object> getRelationshipById(int id) {
        return relationships.get(id);
    }

    public Map<String, Object> getRelationship(String type, String label1, String from, String label2, String to) {
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return null; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts.get(type).get(countId);
        if (count == 0) { return null; }
        int relId = getRelationshipKeyId(type, 1, node1, node2);

        return relationships.get(relId);
    }

    public Map<String, Object> getRelationship(String type, String label1, String from, String label2, String to, int number) {
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return null; }

        int relId = getRelationshipKeyId(type, number, node1, node2);

        return relationships.get(relId);
    }

    // Relationship Properties
    public Object getRelationshipProperty(int id, String property) {
        Map<String, Object> relationship = relationships.get(id);
        String type = (String)relationship.get("_type");
        int node1 = (int)relationship.get("_incoming_node_id");
        int node2 = (int)relationship.get("_outgoing_node_id");
        int count = (int)relationship.getOrDefault("_count", 1);
        int relId = getRelationshipKeyId(type, count, node1, node2);
        return relationships.get(relId).get(property);
    }

    public Object getRelationshipProperty(String type, String label1, String from, String label2, String to, String property) {
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return null; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts.get(type).get(countId);
        if (count == 0) { return null; }
        int relId = getRelationshipKeyId(type, 1, node1, node2);
        return relationships.get(relId).get(property);
    }

    public Object getRelationshipProperty(String type, String label1, String from, String label2, String to, int number, String property) {
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return null; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts.get(type).get(countId);
        if (count == 0 || count < number) { return null; }
        int relId = getRelationshipKeyId(type, number, node1, node2);
        return relationships.get(relId).get(property);
    }

    private boolean containsMetaProperties(Map<String, Object> properties) {
        for (String key : properties.keySet()) {
            if (key.startsWith("_")) {
                return true;
            }
        }
        return false;
    }

    public boolean updateRelationshipProperties(int id, Map<String, Object> properties) {
        if (containsMetaProperties(properties)) return false;
        relationships.get(id).putAll(properties);
        return true;
    }

    public boolean updateRelationshipProperties(String type, String label1, String from, String label2, String to, Map<String, Object> properties) {
        if (containsMetaProperties(properties)) return false;
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts.get(type).get(countId);
        if (count == 0) { return false; }
        int relId = getRelationshipKeyId(type, 1, node1, node2);

        relationships.get(relId).putAll(properties);
        return true;
    }

    public boolean updateRelationshipProperties(String type, String label1, String from, String label2, String to, int number, Map<String, Object> properties) {
        if (containsMetaProperties(properties)) return false;
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts.get(type).get(countId);
        if (count == 0 || count < number) { return false; }
        int relId = getRelationshipKeyId(type, number, node1, node2);
        relationships.get(relId).putAll(properties);
        return true;
    }

    public boolean deleteRelationshipProperties(int id) {
        Map<String, Object> properties = relationships.get(id);
        for (String key : properties.keySet()) {
            if (!key.startsWith("_")) {
                properties.remove(key);
            }
        }
        return true;
    }

    public boolean deleteRelationshipProperties(String type, String label1, String from, String label2, String to) {
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts.get(type).get(countId);
        if (count == 0 ) { return false; }
        int relId = getRelationshipKeyId(type, 1, node1, node2);

        Map<String, Object> properties = relationships.get(relId);
        for (String key : properties.keySet()) {
            if (!key.startsWith("_")) {
                properties.remove(key);
            }
        }
        return true;
    }

    public boolean deleteRelationshipProperties(String type, String label1, String from, String label2, String to, int number) {
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts.get(type).get(countId);
        if (count == 0 || count < number) { return false; }
        int relId = getRelationshipKeyId(type, number, node1, node2);
        Map<String, Object> properties = relationships.get(relId);
        for (String key : properties.keySet()) {
            if (!key.startsWith("_")) {
                properties.remove(key);
            }
        }
        return true;
    }

    public boolean updateRelationshipProperty(int id, String property, Object value) {
        if (property.startsWith("_")) return false;
        relationships.get(id).put(property, value);
        return true;
    }

    public boolean updateRelationshipProperty(String type, String label1, String from, String label2, String to, String property, Object value) {
        if (property.startsWith("_")) return false;
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts.get(type).get(countId);
        if (count == 0) { return false; }
        int relId = getRelationshipKeyId(type, 1, node1, node2);
        relationships.get(relId).put(property, value);
        return true;
    }

    public boolean updateRelationshipProperty(String type, String label1, String from, String label2, String to, int number, String property, Object value) {
        if (property.startsWith("_")) return false;
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts.get(type).get(countId);
        if (count == 0 || count < number) { return false; }
        int relId = getRelationshipKeyId(type, number, node1, node2);
        relationships.get(relId).put(property, value);
        return true;
    }

    public boolean deleteRelationshipProperty(int id, String property) {
        if (property.startsWith("_")) return false;

        Map<String, Object> properties = relationships.get(id);
        if (properties.containsKey(property)) {
            properties.remove(property);
            return true;
        }
        return false;
    }

    public boolean deleteRelationshipProperty(String type, String label1, String from, String label2, String to, String property) {
        if (property.startsWith("_")) return false;
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts.get(type).get(countId);
        if (count == 0) { return false; }
        int relId = getRelationshipKeyId(type, 1, node1, node2);

        Map<String, Object> properties = relationships.get(relId);
        if (properties.containsKey(property)) {
            properties.remove(property);
            return true;
        }
        return false;
    }

    public boolean deleteRelationshipProperty(String type, String label1, String from, String label2, String to, int number, String property) {
        if (property.startsWith("_")) return false;
        int node1 = getNodeKeyId(label1, from);
        int node2 = getNodeKeyId(label2, to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts.get(type).get(countId);
        if ( count == 0 || count < number) { return false; }
        int relId = getRelationshipKeyId(type, number, node1, node2);

        Map<String, Object> properties = relationships.get(relId);
        if (properties.containsKey(property)) {
            properties.remove(property);
            return true;
        }
        return true;
    }

    // Degrees
    public int getNodeDegree(String label, String identifier) {
        return getNodeDegree(label, identifier, Direction.ALL, new ArrayList<>());
    }

    public int getNodeDegree(String label, String identifier, Direction direction) {
        return getNodeDegree(label, identifier, direction, new ArrayList<>());
    }

    public int getNodeDegree(String label, String identifier, Direction direction, String type) {
        return getNodeDegree(label, identifier, direction, new ArrayList<String>(){{add(type);}});
    }

    public int getNodeDegree(String label, String key, Direction direction, List<String> types) {
        int id = getNodeKeyId(label, key);
        if (id == -1) { return -1; }

        int count = 0;
        List<String> relTypes;
        if (types.isEmpty()) {
            relTypes = new ArrayList<>(related.keySet());
        } else {
            types.retainAll(related.keySet());
            relTypes = types;
        }

        for (String type : relTypes) {
            ReversibleMultiMap rels = related.get(type);
            if (direction != Direction.IN) {
                count += rels.getFromSize(id);
            }
            if (direction != Direction.OUT) {
                count += rels.getToSize(id);
            }
        }
        return count;
    }

    // Traversing
    public List<Map<String, Object>> getOutgoingRelationships(String label, String from) {
        return getOutgoingRelationships(getNodeKeyId(label, from));
    }

    public List<Map<String, Object>> getOutgoingRelationships(int node1) {
        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        for (String type : related.keySet()) {
            for (Integer rel : related.get(type).getRels(node1)) {
                nodeRelationships.add(relationships.get(rel));
            }
        }
        return nodeRelationships;
    }

    public List<Map<String,Object>> getOutgoingRelationships(String type, String label, String from) {
        return getOutgoingRelationships(type, getNodeKeyId(label, from));
    }

    public List<Map<String,Object>> getOutgoingRelationships(String type, int node) {
        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        if (related.containsKey(type)) {
            for (Integer rel : related.get(type).getRels(node)) {
                nodeRelationships.add(relationships.get(rel));
            }
        }
        return nodeRelationships;
    }

    public List<Map<String,Object>> getIncomingRelationships(String label, String to) {
        return getIncomingRelationships(getNodeKeyId(label, to));
    }

    public List<Map<String,Object>> getIncomingRelationships(int node2) {
        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        for (String type : related.keySet()) {
            for (Integer rel : related.get(type).getRelsByValue(node2)) {
                nodeRelationships.add(relationships.get(rel));
            }
        }
        return nodeRelationships;
    }

    public List<Map<String,Object>> getIncomingRelationships(String type, String label, String to) {
        return getIncomingRelationships(type, getNodeKeyId(label, to));
    }

    public List<Map<String,Object>> getIncomingRelationships(String type, int node) {
        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        if (related.containsKey(type)) {
            for (Integer rel : related.get(type).getRelsByValue(node)) {
                nodeRelationships.add(relationships.get(rel));
            }
        }
        return nodeRelationships;
    }

    // Ids
    public List<Integer> getOutgoingRelationshipIds(String label, String from) {
        return getOutgoingRelationshipIds(getNodeKeyId(label, from));
    }

    public List<Integer> getOutgoingRelationshipIds(int node1) {
        List<Integer> relationshipIds = new ArrayList<>();
        for (String type : related.keySet()) {
            relationshipIds.addAll(related.get(type).getRels(node1));
        }
        return relationshipIds;
    }

    public List<Integer> getOutgoingRelationshipIds(String type, String label, String from) {
        return getOutgoingRelationshipIds(type, getNodeKeyId(label, from));
    }

    public List<Integer> getOutgoingRelationshipIds(String type, int node) {
        if (related.containsKey(type)) {
            return new ArrayList<>(related.get(type).getRels(node));
        } else {
            return Collections.emptyList();
        }
    }

    public List<Integer> getIncomingRelationshipIds(String label, String to) {
        return getIncomingRelationshipIds(getNodeKeyId(label, to));
    }

    public List<Integer> getIncomingRelationshipIds(int node2) {
        List<Integer> relationshipIds = new ArrayList<>();
        for (String type : related.keySet()) {
            relationshipIds.addAll(related.get(type).getRelsByValue(node2));
        }
        return relationshipIds;
    }

    public List<Integer> getIncomingRelationshipIds(String type, String label, String to) {
        return getIncomingRelationshipIds(type, getNodeKeyId(label, to));
    }

    public List<Integer> getIncomingRelationshipIds(String type, int node) {
        if (related.containsKey(type)) {
            return new ArrayList<>(related.get(type).getRelsByValue(node));
        } else {
            return Collections.emptyList();
        }
    }

    // Nodes

    public Object[] getOutgoingRelationshipNodes(String type, String label, String from) {
        List<Integer> nodeIds;
        if (related.containsKey(type)) {
            nodeIds = (List<Integer>)related.get(type).getNodes(getNodeKeyId(label, from));
        } else {
            nodeIds = Collections.emptyList();
        }
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getOutgoingRelationshipNodes(String label, String from) {
        List<Integer> nodeIds = new ArrayList<>();
        for (String type : related.keySet()) {
            nodeIds.addAll(related.get(type).getNodes(getNodeKeyId(label, from)));
        }
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getIncomingRelationshipNodes(String type, String label, String to) {
        List<Integer> nodeIds;
        if (related.containsKey(type)) {
            nodeIds = (List<Integer>)related.get(type).getNodesByValue(getNodeKeyId(label, to));
        } else {
            nodeIds = Collections.emptyList();
        }

        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getIncomingRelationshipNodes(String label, String to) {
        List<Integer> nodeIds = new ArrayList<>();
        for (String type : related.keySet()) {
            nodeIds.addAll(related.get(type).getNodesByValue(getNodeKeyId(label, to)));
        }
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getOutgoingRelationshipNodes(String type, Integer from) {
        List<Integer> nodeIds;
        if (related.containsKey(type)) {
            nodeIds = (List<Integer>)related.get(type).getNodes(from);
        } else {
            nodeIds = Collections.emptyList();
        }
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getOutgoingRelationshipNodes(Integer from) {
        List<Integer> nodeIds = new ArrayList<>();
        for (String type : related.keySet()) {
            nodeIds.addAll(related.get(type).getNodes(from));
        }
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getIncomingRelationshipNodes(String type, Integer to) {
        List<Integer> nodeIds;
        if (related.containsKey(type)) {
            nodeIds = (List<Integer>)related.get(type).getNodesByValue(to);
        } else {
            nodeIds = Collections.emptyList();
        }
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getIncomingRelationshipNodes(Integer to) {
        List<Integer> nodeIds = new ArrayList<>();
        for (String type : related.keySet()) {
            nodeIds.addAll(related.get(type).getNodesByValue(to));
        }
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }
    public List<Integer> getOutgoingRelationshipNodeIds(String type, Integer from) {
        if (related.containsKey(type)) {
            return (List<Integer>) related.get(type).getNodes(from);
        } else {
            return Collections.emptyList();
        }
    }

    public List<Integer> getOutgoingRelationshipNodeIds(String type, String label, String from) {
        if (related.containsKey(type)) {
            return (List<Integer>)related.get(type).getNodes(getNodeKeyId(label, from));
        } else {
            return Collections.emptyList();
        }
    }

    public List<Integer> getOutgoingRelationshipNodeIds(Integer from) {
        List<Integer> nodeIds = new ArrayList<>();
        for (String type : related.keySet()) {
            nodeIds.addAll(related.get(type).getNodes(from));
        }
        return nodeIds;
    }

    public List<Integer> getOutgoingRelationshipNodeIds(String label, String from) {
        List<Integer> nodeIds = new ArrayList<>();
        for (String type : related.keySet()) {
            nodeIds.addAll(related.get(type).getNodes(getNodeKeyId(label, from)));
        }
        return nodeIds;
    }

    public List<Integer> getIncomingRelationshipNodeIds(Integer to) {
        List<Integer> nodeIds = new ArrayList<>();
        for (String type : related.keySet()) {
            nodeIds.addAll(related.get(type).getNodesByValue(to));
        }
        return nodeIds;
    }

    public List<Integer> getIncomingRelationshipNodeIds(String label, String to) {
        List<Integer> nodeIds = new ArrayList<>();
        for (String type : related.keySet()) {
            nodeIds.addAll(related.get(type).getNodesByValue(getNodeKeyId(label, to)));
        }
        return nodeIds;
    }

    public List<Integer> getIncomingRelationshipNodeIds(String type, Integer to) {
        if (related.containsKey(type)) {
            return (List<Integer>)related.get(type).getNodesByValue(to);
        } else {
            return Collections.emptyList();
        }
    }

    public List<Integer> getIncomingRelationshipNodeIds(String type, String label, String to) {
        if (related.containsKey(type)) {
            return (List<Integer>)related.get(type).getNodesByValue(getNodeKeyId(label, to));
        } else {
            return Collections.emptyList();
        }
    }

    public Iterator<Map<String, Object>> getAllNodes() {
        ArrayList<ObjectIterator<Object2IntMap.Entry<String>>> iterators = new ArrayList<>();
        nodeKeys.keySet().forEach( key -> iterators.add(nodeKeys.get(key).object2IntEntrySet().fastIterator()));
        return new NodeIterator(Iterators.concat(iterators.iterator())).invoke();
    }

    public Iterator<Map<String,Object>> getNodes(String label) {
        if(nodeKeys.containsKey(label)) {
            ObjectIterator<Object2IntMap.Entry<String>> iter = nodeKeys.get(label).object2IntEntrySet().fastIterator();
            return new NodeIterator(iter).invoke();
        }

        return null;
    }

    public Iterator<Map<String, Object>> getAllRelationships() {
        ArrayList<Iterator<Integer>> iterators = new ArrayList<>();
        related.keySet().forEach( key -> iterators.add(related.get(key).getAllRelsIter()));
        return new RelationshipIterator(Iterators.concat(iterators.iterator())).invoke();
    }

    public Iterator<Map<String, Object>> getRelationships(String type) {
        if (related.containsKey(type)) {
            Iterator<Integer> iter = related.get(type).getAllRelsIter();
            return new RelationshipIterator(iter).invoke();
        }
        return null;
    }

    public boolean related(String label1, String from, String label2, String to) {
        return related(label1, from, label2, to, Direction.ALL, new ArrayList<>());
    }

    public boolean related(String label1, String from, String label2, String to, Direction direction, String type) {
        return related(label1, from, label2, to, direction, new ArrayList<String>(){{ add(type); }});
    }
    public boolean related(String label1, String from, String label2, String to, Direction direction, List<String> types) {
        return related(getNodeKeyId(label1, from), getNodeKeyId(label2, to), direction, types);
    }

    public boolean related(int node1, int node2, Direction direction, List<String> types) {
        if (node1 == -1 || node2 == -1) { return false; }
        List<String> relTypes;
        if (types.isEmpty()) {
            relTypes = new ArrayList<>(related.keySet());
        } else {
            types.retainAll(related.keySet());
            relTypes = types;
        }
        for (int i = relTypes.size()-1; i >= 0; i--) {
            String type = relTypes.get(i);
            if (direction != Direction.IN) {
                if (relationshipKeys.get(type + 1).containsKey(((long)node1 << 32) + node2)) {
                    return true;
                }
            }
            if (direction != Direction.OUT) {
                if (relationshipKeys.get(type + 1).containsKey(((long)node2 << 32) + node1)) {
                    return true;
                }
            }
        }

        return false;
    }


    private class RelationshipIterator {
        private Iterator<Integer> iter;

        RelationshipIterator(Iterator<Integer> iter) {
            this.iter = iter;
        }

        Iterator<Map<String, Object>> invoke() {
            return new Iterator<Map<String, Object>>() {
                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public Map<String, Object> next() {
                    return relationships.get(iter.next());
                }
            };
        }
    }

    private class NodeIterator {
        private Iterator<Object2IntMap.Entry<String>> iter;

        NodeIterator(Iterator<Object2IntMap.Entry<String>> iter) {
            this.iter = iter;
        }

        Iterator<Map<String, Object>> invoke() {
            return new Iterator<Map<String, Object>>() {
                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public Map<String, Object> next() {
                    Map<String, Object> node = null;
                    //try {
                        int id = iter.next().getIntValue();
                        node = nodes.get(id);
                    //} catch (ArrayIndexOutOfBoundsException ex) {
                    //    ex.printStackTrace();
                    //}
                    return node;
                }
            };
        }
    }
}
