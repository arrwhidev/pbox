package com.arrwhi.pbox.client.index;

import com.arrwhi.pbox.client.adapters.IndexDifferenceToMessageAdapter;
import com.arrwhi.pbox.client.index.difference.Difference;
import com.arrwhi.pbox.msg.Message;
import com.arrwhi.pbox.msg.MessageFactory;
import com.arrwhi.pbox.util.PropertiesHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by arran on 30/07/17.
 *
 * Singleton Service for interacting with the Index.
 * i.e. loading, updating, etc.
 */
public enum IndexService {

    INSTANCE;

    private final String sourceDirectory;
    private IndexIO indexio;
    private Index index;

    IndexService() {
        indexio = new IndexIO();
        sourceDirectory = PropertiesHelper.get("sourceDirectory");
    }

    public synchronized void add(File file) {
        try {
            IndexEntry indexEntry = IndexEntryFactory.create(file);
            index.add(indexEntry);
            indexio.write(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Message> load() {
        if(indexio.indexExists()) {
            return loadNotFirstTime();
        } else {
            return loadFirstTime();
        }
    }

    private List<Message> loadFirstTime() {
        this.index = FileSystemIndexer.buildIndex();
        writeToIndex();

        List<Message> messagesToWriteToServer = new ArrayList<>();
        convertIndexEntriesToMessages(index.getEntries(), messagesToWriteToServer);
        return messagesToWriteToServer;
    }

    private void convertIndexEntriesToMessages(List<IndexEntry> entries, List<Message> messages) {
        for(IndexEntry ie : entries) {
            Message msg = MessageFactory.createTransportFileMessage(ie.getAsFile(), sourceDirectory);
            if (msg != null) {
                messages.add(msg);
            }

            if(ie.isDirectory()) {
                convertIndexEntriesToMessages(ie.getEntries(), messages);
            }
        }
    }

    private List<Message> loadNotFirstTime() {
        Index newIndex = FileSystemIndexer.buildIndex();
        Index oldIndex = indexio.read();
        List<Message> messagesToWriteToServer = new ArrayList<>();

        IndexComparator indexComparator = new IndexComparator(oldIndex, newIndex);
        if (!indexComparator.areEqual()) {
            System.out.println("Num differences since last time: " + indexComparator.getDifferences().size());
            indexComparator.getDifferences().stream().forEach(System.out::println);
            messagesToWriteToServer = convertDifferencesToMessages(indexComparator.getDifferences());
        } else {
            System.out.println("No differences since last time.");
        }

        this.index = newIndex;
        writeToIndex();
        return messagesToWriteToServer;
    }

    private List<Message> convertDifferencesToMessages(List<Difference> differences) {
        final IndexDifferenceToMessageAdapter indexDifferenceAdapter = new IndexDifferenceToMessageAdapter(sourceDirectory);
        return differences.stream()
            .map(difference -> indexDifferenceAdapter.adapt(difference))
            .filter(msg -> msg != null)
            .collect(Collectors.toList());
    }

    private synchronized void writeToIndex() {
        indexio.write(index);
    }

    public Index index() {
        return index;
    }

    public synchronized void confirmTransportFileDelivery(String hash) {
        try {
            IndexEntry ie = index.getByHash(hash);
            ie.setSynced(true);
            indexio.write(index);
        } catch (IndexEntryNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public synchronized void confirmDeleteFileDelivery(String path) {
        try {
            IndexEntry ie = index.getByPath(path);
            ie.setDeleted(true);
            indexio.write(index);
        } catch (IndexEntryNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
