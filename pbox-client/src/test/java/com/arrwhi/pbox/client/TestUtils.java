package com.arrwhi.pbox.client;

import com.arrwhi.pbox.client.index.IndexEntry;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by arran on 14/01/17.
 */
public class TestUtils {

    // Need this because we can't guarantee order that files will be read from disk.
    public static IndexEntry getEntryWithName(List<IndexEntry> entries, String name) throws Exception {
        List<IndexEntry> filtered = entries.stream()
                .filter(entry -> entry.getName().equals(name))
                .collect(Collectors.toList());

        if (filtered.size() != 1) {
            throw new Exception("Found zero or more than one matching name " + name);
        }

        return filtered.get(0);
    }
}
