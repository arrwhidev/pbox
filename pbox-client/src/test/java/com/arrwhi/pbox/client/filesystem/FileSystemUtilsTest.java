package com.arrwhi.pbox.client.filesystem;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by arran on 15/01/17.
 */
public class FileSystemUtilsTest {

    final static String ROOT_DIR = "/home/arran";

    @Test
    public void shouldReturnRelativePath() throws Exception {
        String path = FileSystemUtils.getRelativePath(ROOT_DIR, ROOT_DIR + "/testing/hello.gif");
        assertThat(path, is("testing/hello.gif"));
    }
}
