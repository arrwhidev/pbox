package com.arrwhi.pbox.msg;

import com.arrwhi.pbox.msg.metadata.MetaData;

/*
 * Delete File Ack Message
 *
 * 2 byte message type      <from Message>
 * 4 byte metadata length   <from MessageWithMetaData>
 * n byte metadata          <from MessageWithMetaData>
 */
public class DeleteFileAckMessage extends MessageWithMetaData {
    public DeleteFileAckMessage() {
        this(null);
    }
    public DeleteFileAckMessage(MetaData metadata) {
        super(MessageType.DELETE_FILE_ACK, metadata);
    }
}