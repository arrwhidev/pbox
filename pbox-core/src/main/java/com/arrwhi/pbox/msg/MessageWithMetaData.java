package com.arrwhi.pbox.msg;

import io.netty.buffer.ByteBuf;

/*
 * Message with meta data
 *
 * 2 byte message type      <from Message>
 * 4 byte metadata length
 * n byte metadata
 */
public abstract class MessageWithMetaData extends Message {

    private byte[] metaData;

    public MessageWithMetaData(short type, byte[] metaData) {
        super(type);
        this.metaData = metaData;
    }

    public void readFrom(ByteBuf src) {
        super.readFrom(src);
        int metaDataLength = src.readInt();
        metaData = new byte[metaDataLength];
        src.readBytes(metaData);
    }

    public void writeToNewBuffer(ByteBuf dest) {
        super.writeToNewBuffer(dest);
        dest.writeInt(metaData.length);
        dest.writeBytes(metaData);
    }

    public byte[] getMetaData() { return metaData; }
    public void setMetaData(byte[] metaData) { this.metaData = metaData; }

}
