package com.arrwhi.pbox.client.index;

import com.arrwhi.pbox.client.index.difference.Difference;
import com.arrwhi.pbox.client.index.difference.DifferenceType;

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

        checkForRemovedDifferences(oldIndex.getEntries(), differences, newIndex);
        checkForAddedDifferences(newIndex.getEntries(), differences, oldIndex);
    }

    private void checkForRemovedDifferences(List<IndexEntry> entries, List<Difference> differences, Index newIndex) {
        for(IndexEntry oldEntry : entries) {
            if(!newIndex.containsEntry(oldEntry)) {
                Difference difference = new Difference(oldEntry, DifferenceType.REMOVED);
                differences.add(difference);
            }

            if(oldEntry.isDirectory()) {
                checkForRemovedDifferences(oldEntry.getEntries(), differences, newIndex);
            }
        }
    }

    private void checkForAddedDifferences(List<IndexEntry> entries, List<Difference> differences, Index oldIndex) {
        for(IndexEntry newEntry : entries) {
            if(!oldIndex.containsEntry(newEntry)) {
                Difference difference = new Difference(newEntry, DifferenceType.ADDED);
                differences.add(difference);
            }

            if(newEntry.isDirectory()) {
                checkForAddedDifferences(newEntry.getEntries(), differences, oldIndex);
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

