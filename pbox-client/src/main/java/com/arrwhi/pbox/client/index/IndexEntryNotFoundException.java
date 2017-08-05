package com.arrwhi.pbox.client.index;

/**
 * Created by arran on 30/07/17.
 */
public class IndexEntryNotFoundException extends Exception {
    IndexEntryNotFoundException(String hash) {
        super(String.format("Did not find IndexEntry with hash: %s", hash));
    }
}
