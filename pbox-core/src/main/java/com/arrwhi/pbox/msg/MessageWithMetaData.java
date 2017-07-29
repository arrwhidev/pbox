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

    private MetaData metaData;

    public MessageWithMetaData(short type, MetaData metaData) {
        super(type);
        this.metaData = metaData;
    }

    public void readFrom(ByteBuf src) {
        super.readFrom(src);
        int metaDataLength = src.readInt();
        byte[] metaData = new byte[metaDataLength];
        src.readBytes(metaData);
        this.metaData = MetaData.fromJsonBytes(metaData);
    }

    public void writeToNewBuffer(ByteBuf dest) {
        super.writeToNewBuffer(dest);

        byte[] metaDataBytes = MetaData.toJsonBytes(this.metaData);
        dest.writeInt(metaDataBytes.length);
        dest.writeBytes(metaDataBytes);
    }

    public MetaData getMetaData() { return metaData; }
    public void setMetaData(MetaData metaData) { this.metaData = metaData; }

}


