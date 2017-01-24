package com.arrwhi.pbox.msg;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/*
 * Message
 *
 * 2 byte message type
 */
public abstract class Message {
    
    private short type;

    public Message(short type) {
        this.type = type;
    }
    
    public void readFrom(ByteBuf src) {
        type = src.readShort();
    }
    
    public void writeTo(ByteBuf dest) {
        dest.writeShort(type);
    }

    // TODO - rename this method.
    // Write to new buffer and return.
    public ByteBuf writeTo() {
        ByteBuf buf = Unpooled.directBuffer();
        writeTo(buf);
        return buf;
    }
    
    public short getType() { 
        return this.type; 
    }

    @Override
    public String toString() {
        return "Message is: " + MessageFactory.getMessageTypeAsString(type);
    }
}