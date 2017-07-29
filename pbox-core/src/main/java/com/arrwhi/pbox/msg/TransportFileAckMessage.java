package com.arrwhi.pbox.msg;

/*
 * Transport File Ack Message
 * 
 * 2 byte message type      <from Message>
 * 4 byte metadata length   <from MessageWithMetaData>
 * n byte metadata          <from MessageWithMetaData>
 */
public class TransportFileAckMessage extends MessageWithMetaData {
    public TransportFileAckMessage() {
        this(null);
    }
    public TransportFileAckMessage(byte[] metadata) {
        super(MessageFactory.TRANSPORT_FILE_ACK, metadata);
    }
}