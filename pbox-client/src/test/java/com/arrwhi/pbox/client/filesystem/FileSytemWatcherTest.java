package com.arrwhi.pbox.client.filesystem;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by arran on 09/07/16.
 */
public class FileSytemWatcherTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void shouldReturnAllNestedSubDirectoriesForAPath() throws Exception {
        tempDir.newFolder("a");
        tempDir.newFolder("b", "hello");
        tempDir.newFolder("c");
        tempDir.newFolder("d", "e");
        tempDir.newFolder("q", "e", "x", "A");
        tempDir.newFile();

        FileSystemWatcher fileSystemWatcher = new FileSystemWatcher("", null, null);
        Set<File> dirs = fileSystemWatcher.allNestedDirectoriesForPath(new HashSet<>(), tempDir.getRoot());
        assertThat(dirs.size(), equalTo(10));
    }
}
