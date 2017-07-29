package com.arrwhi.pbox.client.io;

import com.arrwhi.pbox.client.adapters.FileSystemEventToMessageAdapter;
import com.arrwhi.pbox.client.filesystem.FileSystemChangeEvent;
import com.arrwhi.pbox.msg.Message;
import com.arrwhi.pbox.util.PropertiesHelper;
import io.netty.channel.Channel;
import java.util.Observable;
import java.util.Observer;

public class MessageWriter implements Observer {

    private Channel channel;
    private FileSystemEventToMessageAdapter adapter;

    public MessageWriter(Channel channel) {
        this.channel = channel;
        this.adapter = new FileSystemEventToMessageAdapter(PropertiesHelper.get("sourceDirectory"));
    }

    @Override
    public void update(Observable o, Object arg) {
        FileSystemChangeEvent changeEvent = (FileSystemChangeEvent) arg;
        Message msg = adapter.adapt(changeEvent.getEvent());
        writeMessage(msg);
    }

    public void writeMessage(Message msg) {
        try {
            if(msg != null) {
                channel.writeAndFlush(msg.writeToNewBuffer());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
