package com.arrwhi.pbox.client.netty;

import com.arrwhi.pbox.client.filesystem.FileSystemWatcher;
import com.arrwhi.pbox.client.index.*;
import com.arrwhi.pbox.msg.Message;
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

        final List<Message> messagesToWriteToServer = IndexService.INSTANCE.load();
        final MessageWriter messageWriter = new MessageWriter(future.channel());
        for (Message msg : messagesToWriteToServer) {
            messageWriter.writeMessage(msg);
        }

        final IndexUpdater indexUpdater = new IndexUpdater();
        final FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(sourceDir);
        fileSystemWatcher.addObserver(indexUpdater);
        fileSystemWatcher.addObserver(messageWriter);

        final Thread fileSystemWatcherThread = new Thread(fileSystemWatcher);
        fileSystemWatcherThread.start();
    }
}
