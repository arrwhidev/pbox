package com.arrwhi.pbox.msg;

import com.arrwhi.pbox.msg.flags.Flags;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/*
 * Message
 *
 * 2 byte message type
 * 1 byte flags
 */
public abstract class Message {
    
    private short type;
    private Flags flags;

    public Message(short type) {
        this.type = type;
        this.flags = new Flags();
    }
    
    public void readFrom(ByteBuf src) {
        type = src.readShort();
        flags = new Flags(src.readByte());
    }
    
    public void writeToNewBuffer(ByteBuf dest) {
        dest.writeShort(type);
        dest.writeByte(flags.getFlags());
    }

    public ByteBuf writeToNewBuffer() {
        ByteBuf buf = Unpooled.directBuffer();
        writeToNewBuffer(buf);
        return buf;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }
    
    public short getType() { 
        return this.type; 
    }

    public Flags getFlags() {
        return this.flags;
    }

    @Override
    public String toString() {
        return "Message is: " + MessageFactory.getMessageTypeAsString(type);
    }
}