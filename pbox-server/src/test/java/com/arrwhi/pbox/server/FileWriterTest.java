package com.arrwhi.pbox.server;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by arran on 22/01/2017.
 */
public class FileWriterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldCreateDirectory() throws Exception {
        final String path = temporaryFolder.getRoot().getAbsolutePath();
        new FileWriter(path).createDir("hello");

        File createdDirectory = new File(path + "/" + "hello");
        assertEquals(createdDirectory.isDirectory(), true);
    }
}
