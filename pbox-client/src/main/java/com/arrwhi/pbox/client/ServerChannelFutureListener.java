package com.arrwhi.pbox.client;

import com.arrwhi.pbox.client.filesystem.FileSystemEventToMessageAdapter;
import com.arrwhi.pbox.client.filesystem.FileSystemIndexer;
import com.arrwhi.pbox.client.filesystem.FileSystemWatcher;
import com.arrwhi.pbox.client.index.Index;
import com.arrwhi.pbox.client.index.IndexComparator;
import com.arrwhi.pbox.client.index.IndexIO;
import com.arrwhi.pbox.client.index.IndexUpdater;
import com.arrwhi.pbox.client.io.MessageWriter;
import com.arrwhi.pbox.util.PropertiesHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

/**
 * Created by arran on 14/01/17.
 */
public class ServerChannelFutureListener implements ChannelFutureListener {

    private final FileSystemEventToMessageAdapter adapter;
    private final String sourceDir;

    public ServerChannelFutureListener() {
        this.sourceDir = PropertiesHelper.get("sourceDirectory");
        this.adapter = new FileSystemEventToMessageAdapter(sourceDir);
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
        MessageWriter messageWriter = new MessageWriter(channel);

        Index index = setupIndex(messageWriter);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        IndexUpdater indexUpdater = new IndexUpdater(index);

        FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(sourceDir, watchService, adapter);
        fileSystemWatcher.addObserver(indexUpdater);
        fileSystemWatcher.addObserver(messageWriter);

        new Thread(fileSystemWatcher).start();
    }

    private Index setupIndex(MessageWriter messageWriter) {
        IndexIO indexIO = new IndexIO(PropertiesHelper.get("indexFilePath"));

        Index newIndex = new Index(sourceDir);
        FileSystemIndexer fileSystemIndexer = new FileSystemIndexer(newIndex, adapter, messageWriter);
        fileSystemIndexer.buildIndex();

        if (indexIO.indexExists()) {
            Index oldIndex = indexIO.read();
            IndexComparator indexComparator = new IndexComparator(oldIndex, newIndex);

            if(!indexComparator.areEqual()) {
                // TODO: Send differences to server.
            }
        }

        // TODO - Only need to write the index if something has changed.
        indexIO.write(newIndex);

        return newIndex;
    }
}
