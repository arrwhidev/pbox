package com.arrwhi.pbox.client.netty;

import com.arrwhi.pbox.client.adapters.IndexDifferenceToMessageAdapter;
import com.arrwhi.pbox.client.factories.MessageFactory;
import com.arrwhi.pbox.client.filesystem.FileSystemIndexer;
import com.arrwhi.pbox.client.filesystem.FileSystemWatcher;
import com.arrwhi.pbox.client.index.*;
import com.arrwhi.pbox.client.index.difference.Difference;
import com.arrwhi.pbox.client.io.MessageWriter;
import com.arrwhi.pbox.msg.Message;
import com.arrwhi.pbox.util.PropertiesHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.io.IOException;
import java.util.List;

/**
 * Created by arran on 14/01/17.
 */
public class ServerChannelFutureListener implements ChannelFutureListener {

    private final String sourceDir;

    public ServerChannelFutureListener() {
        this.sourceDir = PropertiesHelper.get("sourceDirectory");
    }

    @Override
    public void operationComplete(ChannelFuture future) {
        System.out.println("Connected to server!");

        try {
            handleConnectionSuccess(future.channel());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void handleConnectionSuccess(Channel channel) throws IOException {
        final MessageWriter messageWriter = new MessageWriter(channel);

        Index index = setupIndex(messageWriter);
        IndexUpdater indexUpdater = new IndexUpdater(index);

        FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(sourceDir);
        fileSystemWatcher.addObserver(indexUpdater);
        fileSystemWatcher.addObserver(messageWriter);

        new Thread(fileSystemWatcher).start();
    }

    private Index setupIndex(MessageWriter messageWriter) {
        IndexIO indexIO = new IndexIO(PropertiesHelper.get("indexFilePath"));
        final boolean indexExists = indexIO.indexExists();

        Index newIndex = new Index(sourceDir);
        FileSystemIndexer fileSystemIndexer = new FileSystemIndexer(newIndex);
        fileSystemIndexer.buildIndex();

        if (indexExists) {
            Index oldIndex = indexIO.read();

            IndexComparator indexComparator = new IndexComparator(oldIndex, newIndex);
            if(!indexComparator.areEqual()) {
                System.out.println("Num differences since last time: " + indexComparator.getDifferences().size());
                writeDifferences(indexComparator.getDifferences(), messageWriter);
            } else {
                System.out.println("No differences since last time.");
            }
        } else {
            writeIndexEntries(newIndex.getEntries(), messageWriter);
        }

        indexIO.write(newIndex);

        return newIndex;
    }

    private void writeDifferences(List<Difference> differences, MessageWriter messageWriter) {
        final IndexDifferenceToMessageAdapter indexDifferenceAdapter = new IndexDifferenceToMessageAdapter(sourceDir);

        differences.stream()
            .map(difference -> indexDifferenceAdapter.adapt(difference))
            .filter(msg -> msg != null)
            .forEach(msg -> messageWriter.writeMessage(msg));
    }

    private void writeIndexEntries(List<IndexEntry> entries, MessageWriter messageWriter) {
        for(IndexEntry ie : entries) {
            Message msg = MessageFactory.createTransportFileMessage(ie.getAsFile(), sourceDir);
            if (msg != null) {
                messageWriter.writeMessage(msg);
            }

            if(ie.isDirectory()) {
                writeIndexEntries(ie.getEntries(), messageWriter);
            }
        }
    }
}
