//package com.arrwhi.pbox.client.index;
//
//import org.apache.commons.codec.Charsets;
//import org.hamcrest.core.Is;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.TemporaryFolder;
//
//import java.io.File;
//import java.nio.file.Files;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.MatcherAssert.assertThat;
//
///**
// * Created by arran on 16/07/16.
// */
//public class IndexIOTest {
//
//    @Rule
//    public TemporaryFolder tempDir = new TemporaryFolder();
//
//    @Test
//    public void shouldCreateJSONFromIndex() throws Exception {
//        final String rootDir = "/root";
//        Index index = new Index(rootDir);
//        index.add(new IndexEntry("hello.gif", "/root/hello.gif", "555"));
//
//        final String EXPECTED_JSON = "{" +
//            "\"rootDir\":\"/root\"," +
//            "\"entries\":[" +
//                "{\"name\":\"hello.gif\",\"filePath\":\"/root/hello.gif\",\"hash\":\"555\",\"isDirectory\":false}" +
//            "]" +
//        "}";
//
//        String json = new IndexIO(rootDir).toJSON(index);
//        assertThat(json, is(EXPECTED_JSON));
//    }
//
//    @Test
//    public void shouldCreateComplexNestedJSONFromIndex() throws Exception {
//        final String rootDir = "/root";
//        Index index = new Index(rootDir);
//        index.add(new IndexEntry("hello.gif", "/root/hello.gif","555"));
//        index.add(new IndexEntry("folder", "/root/folder","666", Arrays.asList(
//            new IndexEntry("nested1.jpg", "/root/folder/nested1.jpg","777"),
//            new IndexEntry("nested2.jpg", "/root/folder/nested2.jpg","888")
//        )));
//
//        final String EXPECTED_JSON = "{" +
//            "\"rootDir\":\"/root\"," +
//            "\"entries\":[" +
//                "{\"name\":\"hello.gif\",\"filePath\":\"/root/hello.gif\",\"hash\":\"555\",\"isDirectory\":false}," +
//                "{\"name\":\"folder\",\"filePath\":\"/root/folder\",\"hash\":\"666\",\"isDirectory\":true,\"entries\":[" +
//                    "{\"name\":\"nested1.jpg\",\"filePath\":\"/root/folder/nested1.jpg\",\"hash\":\"777\",\"isDirectory\":false}," +
//                    "{\"name\":\"nested2.jpg\",\"filePath\":\"/root/folder/nested2.jpg\",\"hash\":\"888\",\"isDirectory\":false}" +
//                "]}" +
//            "]" +
//        "}";
//
//        String json = new IndexIO(rootDir).toJSON(index);
//        assertThat(json, is(EXPECTED_JSON));
//    }
//
//    @Test
//    public void shouldCreateIndexFromJSON() throws Exception {
//        final String JSON = "{" +
//            "\"rootDir\":\"/home/arran\"," +
//            "\"entries\":[" +
//                "{\"name\":\"hello.gif\",\"filePath\":\"/home/arran/hello.gif\",\"hash\":\"555\",\"isDirectory\":false}," +
//                "{\"name\":\"folder\",\"filePath\":\"/home/arran/folder\",\"hash\":\"666\",\"isDirectory\":true,\"entries\":[" +
//                    "{\"name\":\"nested1.jpg\",\"filePath\":\"/home/arran/folder/nested1.jpg\",\"hash\":\"777\",\"isDirectory\":false}," +
//                    "{\"name\":\"nested2.jpg\",\"filePath\":\"/home/arran/folder/nested2.jpg\",\"hash\":\"888\",\"isDirectory\":false}" +
//                "]}" +
//            "]" +
//        "}";
//
//        final File file = tempDir.newFile("index");
//        IndexIO indexio = new IndexIO(file.getAbsolutePath());
//        Index index = indexio.fromJSON(JSON);
//
//        assertThat(index.getRootDir(), is("/home/arran"));
//        assertThat(index.getEntries().size(), is(2));
//
//        assertThat(index.getEntries().get(0).getName(), is("hello.gif"));
//        assertThat(index.getEntries().get(0).getFilePath(), is("/home/arran/hello.gif"));
//        assertThat(index.getEntries().get(0).getHash(), is("555"));
//        assertThat(index.getEntries().get(0).isDirectory(), is(false));
//
//        assertThat(index.getEntries().get(1).getName(), is("folder"));
//        assertThat(index.getEntries().get(1).getFilePath(), is("/home/arran/folder"));
//        assertThat(index.getEntries().get(1).getHash(), is("666"));
//        assertThat(index.getEntries().get(1).isDirectory(), is(true));
//        assertThat(index.getEntries().get(1).getEntries().size(), is(2));
//
//        assertThat(index.getEntries().get(1).getEntries().get(0).getName(), is("nested1.jpg"));
//        assertThat(index.getEntries().get(1).getEntries().get(0).getFilePath(), is("/home/arran/folder/nested1.jpg"));
//        assertThat(index.getEntries().get(1).getEntries().get(0).getHash(), is("777"));
//        assertThat(index.getEntries().get(1).getEntries().get(0).isDirectory(), is(false));
//
//        assertThat(index.getEntries().get(1).getEntries().get(1).getName(), is("nested2.jpg"));
//        assertThat(index.getEntries().get(1).getEntries().get(1).getFilePath(), is("/home/arran/folder/nested2.jpg"));
//        assertThat(index.getEntries().get(1).getEntries().get(1).getHash(), is("888"));
//        assertThat(index.getEntries().get(1).getEntries().get(1).isDirectory(), is(false));
//    }
//
//    @Test
//    public void shouldWriteIndexToDiskAndReadAgain() throws Exception {
//        final File file = tempDir.newFile("index");
//        Index index = new Index(file.getAbsolutePath());
//
//        index.add(new IndexEntry("file1.txt", file.getAbsolutePath() + "/file1.txt","111"));
//        index.add(new IndexEntry("file2.txt", file.getAbsolutePath() + "/file2.txt", "222"));
//        IndexIO indexIO = new IndexIO(file.getAbsolutePath());
//
//        List<String> lines = Files.readAllLines(file.toPath(), Charsets.UTF_8);
//        assertThat(lines.size(), is(0));
//
//        indexIO.write(index);
//        lines = Files.readAllLines(file.toPath(), Charsets.UTF_8);
//        assertThat(lines.size(), is(1));
//
//        Index indexFromDisk = indexIO.read();
//        assertThat(indexFromDisk.getRootDir(), is(file.getAbsolutePath()));
//        assertThat(indexFromDisk.getEntries().size(), Is.is(2));
//        assertThat(indexFromDisk.getEntries().get(0).getFilePath(), Is.is(file.getAbsolutePath() + "/file1.txt"));
//        assertThat(indexFromDisk.getEntries().get(0).getName(), Is.is("file1.txt"));
//        assertThat(indexFromDisk.getEntries().get(0).getHash(), Is.is("111"));
//        assertThat(indexFromDisk.getEntries().get(1).getFilePath(), Is.is(file.getAbsolutePath() + "/file2.txt"));
//        assertThat(indexFromDisk.getEntries().get(1).getName(), Is.is("file2.txt"));
//        assertThat(indexFromDisk.getEntries().get(1).getHash(), Is.is("222"));
//    }
//
//    @Test
//    public void shouldReturnTrue_whenIndexExists() throws Exception {
//        final File file = tempDir.newFile("index");
//        IndexIO io = new IndexIO(file.getAbsolutePath());
//        assertThat(io.indexExists(), is(true));
//    }
//
//    @Test
//    public void shouldReturnFalse_whenIndexDoesNotExist() throws Exception {
//        IndexIO io = new IndexIO("/this/file/does/not/exist");
//        assertThat(io.indexExists(), is(false));
//    }
//}
