package com.arrwhi.pbox.client.factories;

import com.arrwhi.pbox.json.MetaData;
import com.arrwhi.pbox.msg.DeleteFileMessage;
import com.arrwhi.pbox.msg.Message;
import com.arrwhi.pbox.msg.TransportFileMessage;

import java.io.File;
import java.io.IOException;

import static com.arrwhi.pbox.client.filesystem.FileSystemUtils.getRelativePath;
import static com.arrwhi.pbox.client.filesystem.FileSystemUtils.readBytes;

/**
 * Created by arran on 25/01/17.
 */
public class MessageFactory {

    public static Message createTransportFileMessage(File f, String rootDir) {
        MetaData metadata = new MetaData();
        String relative = getRelativePath(rootDir, f.toString());
        metadata.setTo(relative);

        // NOTE: 0 length payload signifies create a directory.
        byte[] payload = new byte[0];
        if(!f.isDirectory()) {
            try {
                payload = readBytes(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new TransportFileMessage(MetaData.toJsonBytes(metadata), payload);
    }

    public static Message createDeleteFileMessage(File f, String rootDir) {
        if(f.isDirectory()) {
            return null;
        } else {
            MetaData metadata = new MetaData();
            String relative = getRelativePath(rootDir, f.toString());
            metadata.setFrom(relative);

            return new DeleteFileMessage(MetaData.toJsonBytes(metadata));
        }
    }

    // TODO...
    public static Message createModifyFileMessage(File f, String rootDir) {
        return null;
    }
}
