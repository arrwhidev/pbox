package com.arrwhi.pbox.msg;

/*
 * Transport File Ack Message
 * 
 * 2 byte message type      <from Message>
 */
public class TransportFileAckMessage extends Message {
    public TransportFileAckMessage() {
        super(MessageFactory.TRANSPORT_FILE_ACK);
    }
}