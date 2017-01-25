package com.arrwhi.pbox.client.index.difference;

import com.arrwhi.pbox.client.index.IndexEntry;

/**
 * Created by arran on 25/01/17.
 */
public class Difference {

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
