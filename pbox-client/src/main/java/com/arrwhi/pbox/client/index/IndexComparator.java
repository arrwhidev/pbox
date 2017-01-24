package com.arrwhi.pbox.client.index;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arran on 22/01/2017.
 */
// TODO - Implement MOVED/RENAMED (deleted and added with same hash).

public class IndexComparator {

    private List<Difference> differences;

    public IndexComparator(Index oldIndex, Index newIndex) {
        this.differences = new ArrayList<>();

        for(IndexEntry oldEntry : oldIndex.getEntries()) {
            if(!newIndex.containsEntry(oldEntry)) {
                Difference difference = new Difference(oldEntry, DifferenceType.REMOVED);
                differences.add(difference);
            }
        }

        for(IndexEntry newEntry : newIndex.getEntries()) {
            if(!oldIndex.containsEntry(newEntry)) {
                Difference difference = new Difference(newEntry, DifferenceType.ADDED);
                differences.add(difference);
            }
        }
    }

    public boolean areEqual() {
        return differences.size() == 0;
    }

    public List<Difference> getDifferences() {
        return differences;
    }
}

enum DifferenceType { REMOVED, ADDED }

class Difference {

    private DifferenceType type;
    private IndexEntry entry;

    public Difference(IndexEntry entry, DifferenceType type) {
        this.type = type;
        this.entry = entry;
    }

    public DifferenceType getType() {
        return type;
    }

    public IndexEntry getEntry() {
        return entry;
    }
}