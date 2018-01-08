package com.uranusdb.graph;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ReversibleMultiMap {

    private Multimap<Integer, Integer> from2to = ArrayListMultimap.create();
    private Multimap<Integer, Integer> from2rel = ArrayListMultimap.create();
    private Multimap<Integer, Integer> to2from = ArrayListMultimap.create();
    private Multimap<Integer, Integer> to2rel = ArrayListMultimap.create();

    public int size() {
        return from2to.size();
    }

    public boolean isEmpty() {
        return from2to.isEmpty();
    }

    public boolean containsNode(int node) {
        return from2to.containsKey(node);
    }

    public boolean containsOtherNode(int node) {
        return to2from.containsKey(node);
    }

    public boolean containsEntry(int from, int to, int rel) {
        return from2to.containsEntry(from, to) && from2rel.containsEntry(from, rel);
    }

    public boolean put(Integer from, Integer to, Integer rel) {
        from2to.put(from, to);
        from2rel.put(from, rel);
        to2from.put(to, from);
        return to2rel.put(to, rel);
    }

    public boolean removeRelationship(Integer from, Integer to, Integer rel) {
        from2rel.remove(from, rel);
        from2to.remove(from, to);
        to2from.remove(to, from);
        return to2rel.remove(to, rel);
    }

    public void removeAll(int key) {
        ArrayList<Integer> removedRels = new ArrayList(from2rel.removeAll(key));
        Collection<Integer> removed = from2to.removeAll(key);
        int count = 0;
        for (Integer value : removed) {
            to2from.remove(value, key);
            to2rel.remove(value, removedRels.get(count));
            count++;
        }
    }

    public void clear() {
        from2to.clear();
        from2rel.clear();
        to2from.clear();
        to2rel.clear();
    }

    public Collection<Integer> getNodes(Integer from) {
        return from2to.get(from);
    }

    public Collection<Integer> getNodesByValue(Integer to) {
        return to2from.get(to);
    }

    public Collection<Integer> getRels(Integer from) {
        return from2rel.get(from);
    }

    public Collection<Integer> getRelsByValue(Integer to) {
        return to2rel.get(to);
    }

    public Collection<Integer> getAllRels() {

        return from2rel.values();
    }

    public Iterator<Integer> getAllRelsIter() {
        return new ArrayList<Integer> () {{ addAll(from2rel.values()); }}.iterator();
    }

    public int getFromSize(Integer from) {
        return from2to.get(from).size();
    }

    public int getToSize(Integer to) {
        return to2from.get(to).size();
    }
}
