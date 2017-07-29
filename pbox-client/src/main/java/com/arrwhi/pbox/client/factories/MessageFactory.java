package com.arrwhi.pbox.client.factories;

import com.arrwhi.pbox.crypto.HashFactory;
import com.arrwhi.pbox.json.MetaData;
import com.arrwhi.pbox.msg.DeleteFileMessage;
import com.arrwhi.pbox.msg.Message;
import com.arrwhi.pbox.msg.TransportFileMessage;
import com.arrwhi.pbox.msg.flags.Flags;
import com.arrwhi.pbox.util.PathHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by arran on 25/01/17.
 */
public class MessageFactory {

    public static Message createTransportFileMessage(File f, String rootDir) {
        MetaData metadata = new MetaData();
        String relative = PathHelper.getRelativePath(rootDir, f.toString());
        metadata.setTo(relative);
        try {
            metadata.setHash(HashFactory.create(f));
        } catch (Exception e) {
            // TODO: handle this.
            e.printStackTrace();
        }

        byte[] payload = new byte[0];
        if(!f.isDirectory()) {
            try {
                payload = Files.readAllBytes(Paths.get(f.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return setFlagsOnMessage(
            new TransportFileMessage(MetaData.toJsonBytes(metadata), payload),
            f.isDirectory()
        );
    }

    public static Message createDeleteFileMessage(File f, String rootDir) {
        MetaData metadata = new MetaData();
        String relative = PathHelper.getRelativePath(rootDir, f.toString());
        metadata.setFrom(relative);

        return setFlagsOnMessage(
            new DeleteFileMessage(MetaData.toJsonBytes(metadata)),
            f.isDirectory()
        );
    }

    // TODO...
    public static Message createModifyFileMessage(File f, String rootDir) {
        return null;
    }

    private static Message setFlagsOnMessage(Message msg, boolean isDirectory) {
        Flags flags = new Flags();
        flags.setIsDirectory(isDirectory);
        msg.setFlags(flags);
        return msg;
    }
}
