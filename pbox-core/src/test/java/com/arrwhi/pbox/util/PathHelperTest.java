package com.arrwhi.pbox.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by arran on 29/07/17.
 */
public class PathHelperTest {
    final static String ROOT_DIR = "/home/arran";

    @Test
    public void getRelativePath_shouldReturnRelativePath() throws Exception {
        String path = PathHelper.getRelativePath(ROOT_DIR, ROOT_DIR + "/testing/hello.gif");
        assertThat(path, is("testing/hello.gif"));
    }
}