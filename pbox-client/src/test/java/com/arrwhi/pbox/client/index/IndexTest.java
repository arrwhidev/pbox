package com.arrwhi.pbox.client.index;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by arran on 16/07/16.
 */
public class IndexTest {

    private static final String ROOT_DIR = "/home/arran/pbox/";

    @Test
    public void shouldAddEntryToIndex() throws Exception {
        final String path = "a/b/hello.txt";
        final String hash = "1234";

        Index index = new Index(ROOT_DIR);
        index.add(new IndexEntry("z", path, hash));

        assertThat(index.getEntries().size(), is(1));
        assertThat(index.getEntries().get(0).getFilePath(), is(path));
        assertThat(index.getEntries().get(0).getHash(), is(hash));
    }

    @Test
    public void shouldReturnTrue_whenIndexContainsAnEntryWithSameValues() throws Exception {
        Index index = new Index(ROOT_DIR);
        index.add(new IndexEntry("a1", ROOT_DIR + "a1", "hash1"));
        index.add(new IndexEntry("a2", ROOT_DIR + "a2", "hash2"));

        assertThat(index.containsEntry(new IndexEntry("a1", ROOT_DIR + "a1", "hash1")), equalTo(true));
    }

    @Test
    public void shouldReturnTrue_whenIndexContainsAnEntryWithSameValuesInsideADirectory() throws Exception {
        Index index = new Index(ROOT_DIR);
        IndexEntry dirEntry = new IndexEntry("dir1", ROOT_DIR + "dir1", "");
        dirEntry.setIsDirectory(true);
        dirEntry.setEntries(Arrays.asList(
                new IndexEntry("file1", ROOT_DIR + "dir1/file1", "hash1")
        ));
        index.add(dirEntry);

        assertThat(index.containsEntry(
            new IndexEntry("file1", ROOT_DIR + "dir1/file1", "hash1")
        ), equalTo(true));
    }

    @Test
    public void shouldReturnFalse_whenIndexDoesNotContainEntry() throws Exception {
        Index index = new Index(ROOT_DIR);
        index.add(new IndexEntry("a1", ROOT_DIR + "a1", "hash1"));
        index.add(new IndexEntry("a2", ROOT_DIR + "a2", "hash2"));

        assertThat(index.containsEntry(new IndexEntry("ASD!D", ROOT_DIR + "a1", "hash1")), equalTo(false));
    }

    @Test
    public void getByHash_shouldReturnIndexEntryWithMatchingHash() throws Exception {
        IndexEntry ie1 = new IndexEntry("1", "/1", "hash1");
        IndexEntry ie2 = new IndexEntry("2", "/2", "hash2");
        Index index = new Index(ROOT_DIR, Arrays.asList(ie1, ie2));

        IndexEntry found = index.getByHash("hash2");
        assertThat(found, is(notNullValue()));
        assertThat(found.equals(ie2), is(true));
    }

    @Test
    public void getByHash_shouldReturnIndexEntryWithMatchingHash_whenNested() throws Exception {
        IndexEntry ie1 = new IndexEntry("1", "/1", "hash1");
        IndexEntry ie2 = new IndexEntry("2", "/2", "hash2");
        IndexEntry ie3 = new IndexEntry("3", "/3", "hash3");
        IndexEntry ie4 = new IndexEntry("4", "/4", "hash4");
        ie1.setEntries(Arrays.asList(ie3));
        ie2.setEntries(Arrays.asList(ie4));
        Index index = new Index(ROOT_DIR, Arrays.asList(ie1, ie2));

        IndexEntry found = index.getByHash("hash4");
        assertThat(found, is(notNullValue()));
        assertThat(found.equals(ie4), is(true));
    }
}
