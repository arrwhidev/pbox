package com.arrwhi.pbox.msg;

import com.arrwhi.pbox.crypto.HashFactory;
import com.arrwhi.pbox.exception.InvalidMessageTypeException;

import com.arrwhi.pbox.msg.flags.Flags;
import com.arrwhi.pbox.msg.metadata.MetaData;
import com.arrwhi.pbox.msg.metadata.MetaDataBuilder;
import com.arrwhi.pbox.util.PathHelper;
import io.netty.buffer.ByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.arrwhi.pbox.msg.MessageType.DELETE_FILE;

public class MessageFactory {

    private static Logger LOGGER = LogManager.getLogger();
    
    private MessageFactory() {}

    public static Message fromBuffer(ByteBuf buf, MessageType type) {
        try {
            Message message = type.getClazz().newInstance();
            message.readFrom(buf);
            return message;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    ////////////////////////

    public static Message createTransportFileMessage(File f, String rootDir) {
        String relative = PathHelper.getRelativePath(rootDir, f.toString());
        String hash = HashFactory.create(f);
        MetaData metaData = new MetaDataBuilder().withTo(relative).withHash(hash).build();

        byte[] payload = new byte[0];
        if(!f.isDirectory()) {
            try {
                payload = Files.readAllBytes(Paths.get(f.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return setFlagsOnMessage(
                new TransportFileMessage(metaData, payload),
                f.isDirectory()
        );
    }

    public static TransportFileAckMessage createTransportFileAckMessage(String hash) {
        MetaData metadata = new MetaDataBuilder().withHash(hash).build();
        return new TransportFileAckMessage(metadata);
    }

    public static DeleteFileAckMessage createDeleteFileAckMessage(String path) {
        MetaData metadata = new MetaDataBuilder().withFrom(path).build();
        return new DeleteFileAckMessage(metadata);
    }

    public static Message createDeleteFileMessage(File f, String rootDir) {
        String relative = PathHelper.getRelativePath(rootDir, f.toString());
        MetaData metaData = new MetaDataBuilder().withFrom(relative).build();

        return setFlagsOnMessage(
                new DeleteFileMessage(metaData),
                f.isDirectory()
        );
    }

    // TODO...
    public static Message createModifyFileMessage(File f, String rootDir) {
        return null;
    }

//    private static void checkMessageType(MessageType expectedType, ByteBuf src) throws InvalidMessageTypeException {
//        short type = src.readShort();
//        src.resetReaderIndex();
//        if(expectedType.getType() != type) {
//            String msg = String.format("Invalid message type. Expected %d got %d", expectedType.getType(), type);
//            throw new InvalidMessageTypeException(msg);
//        }
//    }

    private static Message setFlagsOnMessage(Message msg, boolean isDirectory) {
        Flags flags = new Flags();
        flags.setIsDirectory(isDirectory);
        msg.setFlags(flags);
        return msg;
    }
}