package com.arrwhi.pbox.client.filesystem;

import com.arrwhi.pbox.client.index.Index;
import com.arrwhi.pbox.client.index.IndexEntry;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static com.arrwhi.pbox.client.TestUtils.getEntryWithName;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by arran on 14/01/17
 */
public class FileSystemIndexerTest {

    @Test
    public void shouldGenerateInitialIndex_whenIndexIsEmpty() throws Exception {
        File rootDir = givenADirectoryWithFiles();
        File nestedDir = givenDirectoryWithFilesInsideDirectory(rootDir);

        Index index = new Index(rootDir.getAbsolutePath());
        new FileSystemIndexer(index).buildIndex();

        assertThat(index.getEntries().size(), is(3));

        IndexEntry one = getEntryWithName(index.getEntries(), "1.txt");
        assertThat(one.getName(), is("1.txt"));
        assertThat(one.getFilePath(), is(rootDir.getAbsolutePath() + "/1.txt"));
        assertThat(one.isDirectory(), is(false));

        IndexEntry two = getEntryWithName(index.getEntries(), "2.txt");
        assertThat(two.getName(), is("2.txt"));
        assertThat(two.getFilePath(), is(rootDir.getAbsolutePath() + "/2.txt"));
        assertThat(two.isDirectory(), is(false));

        IndexEntry three = getEntryWithName(index.getEntries(), nestedDir.getName());
        assertThat(three.getName(), is(nestedDir.getName()));
        assertThat(three.getFilePath(), is(nestedDir.getAbsolutePath()));
        assertThat(three.isDirectory(), is(true));
        assertThat(three.getEntries().size(), is(2));

        IndexEntry nestedOne = getEntryWithName(three.getEntries(), "img1.jpg");
        assertThat(nestedOne.getName(), is("img1.jpg"));
        assertThat(nestedOne.getFilePath(), is(nestedDir.getAbsolutePath() + "/img1.jpg"));
        assertThat(nestedOne.isDirectory(), is(false));

        IndexEntry nestedTwo = getEntryWithName(three.getEntries(), "img2.jpg");
        assertThat(nestedTwo.getName(), is("img2.jpg"));
        assertThat(nestedTwo.getFilePath(), is(nestedDir.getAbsolutePath() + "/img2.jpg"));
        assertThat(nestedTwo.isDirectory(), is(false));
    }

    @Test
    public void shouldSetHashToEmptyString_whenDirectory() throws Exception {
        File rootDir = givenADirectoryWithAFolder();

        Index index = new Index(rootDir.getAbsolutePath());
        new FileSystemIndexer(index).buildIndex();

        IndexEntry folder = getEntryWithName(index.getEntries(), "folder");
        assertThat(folder.getHash(), is(""));
    }

    private File givenADirectoryWithAFolder() throws IOException {
        TemporaryFolder tempDir = new TemporaryFolder();
        tempDir.create();

        tempDir.newFolder("folder");

        return tempDir.getRoot();
    }

    private File givenADirectoryWithFiles() throws IOException {
        TemporaryFolder tempDir = new TemporaryFolder();
        tempDir.create();

        tempDir.newFile("1.txt");
        tempDir.newFile("2.txt");

        return tempDir.getRoot();
    }

    private File givenDirectoryWithFilesInsideDirectory(File parent) throws IOException {
        TemporaryFolder nestedFolder = new TemporaryFolder(parent);
        nestedFolder.create();

        nestedFolder.newFile("img1.jpg");
        nestedFolder.newFile("img2.jpg");

        return nestedFolder.getRoot();
    }
}