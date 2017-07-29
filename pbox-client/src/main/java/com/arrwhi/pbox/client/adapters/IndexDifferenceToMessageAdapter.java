package com.arrwhi.pbox.client.adapters;

import com.arrwhi.pbox.client.index.difference.Difference;
import com.arrwhi.pbox.msg.Message;
import com.arrwhi.pbox.msg.MessageFactory;

/**
 * Created by arran on 25/01/17.
 */
public class IndexDifferenceToMessageAdapter {

    private final String rootDir;

    public IndexDifferenceToMessageAdapter(String rootDir) {
        this.rootDir = rootDir;
    }

    public Message adapt(Difference difference) {
        switch (difference.getType()) {
            case ADDED:
                return MessageFactory.createTransportFileMessage(difference.getEntry().getAsFile(), rootDir);
            case REMOVED:
                return MessageFactory.createDeleteFileMessage(difference.getEntry().getAsFile(), rootDir);
            default:
                return null;
        }
    }
}
