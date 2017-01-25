package com.arrwhi.pbox.client.index;

import com.arrwhi.pbox.client.index.difference.Difference;
import com.arrwhi.pbox.client.index.difference.DifferenceType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by arran on 22/01/2017.
 */
public class IndexComparatorTest {

    @Test
    public void shouldReturnEmptyList_whenIndexesAreTheSame() {
        Index oldIndex = new Index("/");
        Index newIndex = new Index("/");

        for(int i = 0; i < 10; i++) {
            IndexEntry entry = new IndexEntry("file" + i, "/file" + i, "hash" + i);
            oldIndex.add(entry);
            newIndex.add(entry);
        }

        IndexComparator comparator = new IndexComparator(oldIndex, newIndex);
        assertTrue(comparator.areEqual());
        assertThat(comparator.getDifferences().size(), equalTo(0));
    }

    @Test
    public void shouldReturnARemovedDifference_whenFileIsRemovedInNewIndex() throws Exception {
        Index oldIndex = new Index("/");
        Index newIndex = new Index("/");

        oldIndex.add(new IndexEntry("file1", "/file1", "hash1"));
        oldIndex.add(new IndexEntry("file2", "/file2", "hash2"));
        newIndex.add(new IndexEntry("file1", "/file1", "hash1"));

        IndexComparator comparator = new IndexComparator(oldIndex, newIndex);

        assertFalse(comparator.areEqual());
        assertThat(comparator.getDifferences().size(), equalTo(1));

        Difference difference = comparator.getDifferences().get(0);
        assertThat(difference.getType(), equalTo(DifferenceType.REMOVED));
        assertThat(difference.getEntry().getName(), equalTo("file2"));
        assertThat(difference.getEntry().getFilePath(), equalTo("/file2"));
        assertThat(difference.getEntry().getHash(), equalTo("hash2"));
    }

    @Test
    public void shouldReturnARemovedDifference_whenDirectoryIsRemovedInNewIndex() throws Exception {
        Index oldIndex = new Index("/");
        Index newIndex = new Index("/");

        oldIndex.add(new IndexEntry("dir1", "/dir1", "", Collections.emptyList()));

        IndexComparator comparator = new IndexComparator(oldIndex, newIndex);

        assertFalse(comparator.areEqual());
        assertThat(comparator.getDifferences().size(), equalTo(1));

        Difference difference = comparator.getDifferences().get(0);
        assertThat(difference.getType(), equalTo(DifferenceType.REMOVED));
        assertThat(difference.getEntry().getName(), equalTo("dir1"));
        assertThat(difference.getEntry().getFilePath(), equalTo("/dir1"));
        assertThat(difference.getEntry().getHash(), equalTo(""));
        assertThat(difference.getEntry().isDirectory(), equalTo(true));
    }

    @Test
    public void shouldReturnAnAddedDifference_whenFileIsAddedInNewIndex() throws Exception {
        Index oldIndex = new Index("/");
        Index newIndex = new Index("/");

        oldIndex.add(new IndexEntry("file1", "/file1", "hash1"));
        newIndex.add(new IndexEntry("file1", "/file1", "hash1"));
        newIndex.add(new IndexEntry("file2", "/file2", "hash2"));

        IndexComparator comparator = new IndexComparator(oldIndex, newIndex);

        assertFalse(comparator.areEqual());
        assertThat(comparator.getDifferences().size(), equalTo(1));

        Difference difference = comparator.getDifferences().get(0);
        assertThat(difference.getType(), equalTo(DifferenceType.ADDED));
        assertThat(difference.getEntry().getName(), equalTo("file2"));
        assertThat(difference.getEntry().getFilePath(), equalTo("/file2"));
        assertThat(difference.getEntry().getHash(), equalTo("hash2"));
    }

    @Test
    public void shouldReturnAnAddedDifference_whenDirectoryIsAddedInNewIndex() throws Exception {
        Index oldIndex = new Index("/");
        Index newIndex = new Index("/");

        newIndex.add(new IndexEntry("dir1", "/dir1", "", Collections.emptyList()));

        IndexComparator comparator = new IndexComparator(oldIndex, newIndex);

        assertFalse(comparator.areEqual());
        assertThat(comparator.getDifferences().size(), equalTo(1));

        Difference difference = comparator.getDifferences().get(0);
        assertThat(difference.getType(), equalTo(DifferenceType.ADDED));
        assertThat(difference.getEntry().getName(), equalTo("dir1"));
        assertThat(difference.getEntry().getFilePath(), equalTo("/dir1"));
        assertThat(difference.getEntry().getHash(), equalTo(""));
        assertThat(difference.getEntry().isDirectory(), equalTo(true));
    }

    @Test
    public void shouldReturnAnAddedDifference_whenFileIsAddedInsideDirectoryInNewIndex() throws Exception {
        Index oldIndex = new Index("/");
        Index newIndex = new Index("/");

        oldIndex.add(new IndexEntry("dir1", "/dir1", "", Collections.emptyList()));
        newIndex.add(new IndexEntry("dir1", "/dir1", "", Arrays.asList(
            new IndexEntry("file1", "/dir1/file1", "hash1")
        )));

        IndexComparator comparator = new IndexComparator(oldIndex, newIndex);

        assertFalse(comparator.areEqual());
        assertThat(comparator.getDifferences().size(), equalTo(1));

        Difference difference = comparator.getDifferences().get(0);
        assertThat(difference.getType(), equalTo(DifferenceType.ADDED));
        assertThat(difference.getEntry().getName(), equalTo("file1"));
        assertThat(difference.getEntry().getFilePath(), equalTo("/dir1/file1"));
        assertThat(difference.getEntry().getHash(), equalTo("hash1"));
        assertThat(difference.getEntry().isDirectory(), equalTo(false));
    }
}
