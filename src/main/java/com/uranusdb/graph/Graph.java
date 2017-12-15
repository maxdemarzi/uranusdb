package com.uranusdb.graph;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Graph {
    void clear();

    // Relationship Types
    Set<String> getRelationshipTypes();
    Map<String, Integer> getRelationshipTypesCount();
    Integer getRelationshipTypeCount(String type);

    // Nodes
    boolean addNode(String label, String key);
    boolean addNode(String label, String key, Map<String, Object> properties);
    boolean removeNode(String label, String key);
    Map<String, Object> getNode(String label, String key);
    int getNodeId(String label, String key);

    // Node Properties
    Object getNodeProperty(String label, String key, String property);
    boolean updateNodeProperties(String label, String key, Map<String, Object> properties);
    boolean deleteNodeProperties(String label, String key);
    boolean updateNodeProperty(String label, String key, String property, Object value);
    boolean deleteNodeProperty(String label, String key, String property);

    // Relationships
    boolean addRelationship(String type, String label1, String from, String label2, String to);
    boolean addRelationship(String type, String label1, String from, String label2, String to, Map<String, Object> properties);
    boolean removeRelationship(String type, String label1, String from, String label2, String to);
    boolean removeRelationship(String type, String label1, String from, String label2, String to, int number);
    Map<String, Object> getRelationship(String type, String label1, String from, String label2, String to);
    Map<String, Object> getRelationship(String type, String label1, String from, String label2, String to, int number);

    // Relationship Properties
    Object getRelationshipProperty(String type, String label1, String from, String label2, String to, String property);
    Object getRelationshipProperty(String type, String label1, String from, String label2, String to, int number, String property);
    boolean updateRelationshipProperties(String type, String label1, String from, String label2, String to, Map<String, Object> properties);
    boolean updateRelationshipProperties(String type, String label1, String from, String label2, String to, int number, Map<String, Object> properties);
    boolean deleteRelationshipProperties(String type, String label1, String from, String label2, String to);
    boolean deleteRelationshipProperties(String type, String label1, String from, String label2, String to, int number);
    boolean updateRelationshipProperty(String type, String label1, String from, String label2, String to, String property, Object value);
    boolean updateRelationshipProperty(String type, String label1, String from, String label2, String to, int number, String property, Object value);
    boolean deleteRelationshipProperty(String type, String label1, String from, String label2, String to, String property);
    boolean deleteRelationshipProperty(String type, String label1, String from, String label2, String to, int number, String property);

    // Node Degree
    int getNodeDegree(String label, String key);
    int getNodeDegree(String label, String key, Direction direction);
    int getNodeDegree(String label, String key, Direction direction, String type);
    int getNodeDegree(String label, String key, Direction direction, List<String> types);

    // Traversing
    List<Map<String, Object>> getOutgoingRelationships(String label1, String from);
    List<Map<String, Object>> getOutgoingRelationships(int from);
    List<Map<String, Object>> getOutgoingRelationships(String type, String label1, String from);
    List<Map<String, Object>> getOutgoingRelationships(String type, int from);
    List<Map<String, Object>> getIncomingRelationships(String label1, String from);
    List<Map<String, Object>> getIncomingRelationships(int from);
    List<Map<String, Object>> getIncomingRelationships(String type, String label1, String from);
    List<Map<String, Object>> getIncomingRelationships(String type, int from);

    List<Integer> getOutgoingRelationshipNodeIds(String type, String label1, String from);
    List<Integer> getOutgoingRelationshipNodeIds(String type, Integer from);
    Object[] getOutgoingRelationshipNodes(String type, String label1, String from);
    Object[] getIncomingRelationshipNodes(String type, String label2, String to);

    List<Integer> getIncomingRelationshipNodeIds(String type, String label2, String to);
    List<Integer> getIncomingRelationshipNodeIds(String type, Integer to);
    Object[] getOutgoingRelationshipNodes(String type, Integer from);
    Object[] getIncomingRelationshipNodes(String type, Integer to);

    // Extras
    Iterator<Map<String, Object>> getAllNodes();
    Iterator<Map<String, Object>> getNodes(String label);
    Iterator<Map<String, Object>> getAllRelationships();
    Iterator<Map<String, Object>> getRelationships(String type);

    // Related
    boolean related(String label1, String from, String label2, String to);
    boolean related(String label1, String from, String label2, String to, Direction direction, String type);
    boolean related(String label1, String from, String label2, String to, Direction direction, List<String> types);
}
