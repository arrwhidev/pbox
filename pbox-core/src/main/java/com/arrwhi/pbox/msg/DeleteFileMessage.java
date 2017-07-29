package com.arrwhi.pbox.msg;

import com.arrwhi.pbox.json.MetaData;

/*
 * Delete File Message
 *
 * 2 byte message type      <from Message>
 * 4 byte metadata length   <from MessageWithMetaData>
 * n byte metadata          <from MessageWithMetaData>
 */
public class DeleteFileMessage extends MessageWithMetaData {

    public DeleteFileMessage(MetaData metaData) {
        super(MessageFactory.DELETE_FILE, metaData);
    }

    public DeleteFileMessage() {
        this(null);
    }
}
