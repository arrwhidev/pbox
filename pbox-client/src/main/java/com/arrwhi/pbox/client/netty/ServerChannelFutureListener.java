package com.arrwhi.pbox.client.netty;

import com.arrwhi.pbox.client.adapters.IndexDifferenceToMessageAdapter;
import com.arrwhi.pbox.client.index.FileSystemIndexer;
import com.arrwhi.pbox.client.filesystem.FileSystemWatcher;
import com.arrwhi.pbox.client.index.*;
import com.arrwhi.pbox.client.index.difference.Difference;
import com.arrwhi.pbox.msg.Message;
import com.arrwhi.pbox.msg.MessageFactory;
import com.arrwhi.pbox.util.PropertiesHelper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by arran on 14/01/17.
 */
public class ServerChannelFutureListener implements ChannelFutureListener {

    private final String sourceDir;
    private Logger logger = LogManager.getLogger();

    public ServerChannelFutureListener() {
        this.sourceDir = PropertiesHelper.get("sourceDirectory");
    }

    @Override
    public void operationComplete(ChannelFuture future) {
        logger.info("Connected to server!");

        final MessageWriter messageWriter = new MessageWriter(future.channel());
        final Index index = setupIndex(messageWriter);
        final IndexUpdater indexUpdater = new IndexUpdater(index);
        final FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(sourceDir);
        fileSystemWatcher.addObserver(indexUpdater);
        fileSystemWatcher.addObserver(messageWriter);

        new Thread(fileSystemWatcher).start();
    }

    private Index setupIndex(MessageWriter messageWriter) {
        final IndexIO indexIO = new IndexIO();
        final boolean indexExists = indexIO.indexExists();
        final Index newIndex = FileSystemIndexer.buildIndex();
        indexIO.write(newIndex);

        if (indexExists) {
            Index oldIndex = indexIO.read();
            IndexComparator indexComparator = new IndexComparator(oldIndex, newIndex);
            if(!indexComparator.areEqual()) {
                logger.trace("Num differences since last time: " + indexComparator.getDifferences().size());
                writeDifferences(indexComparator.getDifferences(), messageWriter);
            } else {
                logger.trace("No differences since last time.");
            }
        } else {
            writeIndexEntries(newIndex.getEntries(), messageWriter);
        }

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
