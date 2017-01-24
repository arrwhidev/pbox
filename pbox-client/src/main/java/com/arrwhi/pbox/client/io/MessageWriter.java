package com.arrwhi.pbox.client.io;

import com.arrwhi.pbox.client.filesystem.FileSystemChangeEvent;
import com.arrwhi.pbox.msg.Message;
import io.netty.channel.Channel;
import java.util.Observable;
import java.util.Observer;

public class MessageWriter implements Observer {

    private Channel channel;

    public MessageWriter(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void update(Observable o, Object arg) {
        FileSystemChangeEvent changeEvent = (FileSystemChangeEvent) arg;
        Message msg = changeEvent.getMsg();
        writeMessage(msg);
    }

    public void writeMessage(Message msg) {

        try {
            if(msg != null) {
                channel.writeAndFlush(msg.writeTo());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
