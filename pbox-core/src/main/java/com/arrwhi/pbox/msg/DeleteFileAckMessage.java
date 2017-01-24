package com.arrwhi.pbox.msg;

/*
 * Delete File Ack Message
 *
 * 2 byte message type      <from Message>
 */
public class DeleteFileAckMessage extends Message {
    public DeleteFileAckMessage() {
        super(MessageFactory.DELETE_FILE_ACK);
    }
}