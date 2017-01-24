package com.arrwhi.pbox.msg;

import io.netty.buffer.ByteBuf;

/*
 * Transport File Message
 * 
 * 2 byte message type      <from Message>
 * 4 byte metadata length   <from MessageWithMetaData>
 * n byte metadata          <from MessageWithMetaData>
 * n byte payload
 */
public class TransportFileMessage extends MessageWithMetaData {
    
    private byte[] payload;

    public TransportFileMessage() {
        this(null, null);
    }

    public TransportFileMessage(byte[] metadata, byte[] payload) {
        super(MessageFactory.TRANSPORT_FILE, metadata);
        this.payload = payload;
    }
    
    public void readFrom(ByteBuf src) {
        super.readFrom(src);
        payload = new byte[src.readableBytes()];
        src.readBytes(payload);
    }

    public void writeTo(ByteBuf dest) {
        super.writeTo(dest);
        dest.writeBytes(payload);
    }
    
    public byte[] getPayload() { return payload; }
    public void setPayload(byte[] payload) { this.payload = payload; }
}