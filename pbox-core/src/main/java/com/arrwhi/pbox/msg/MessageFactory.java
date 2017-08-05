package com.arrwhi.pbox.msg;

import com.arrwhi.pbox.crypto.HashFactory;
import com.arrwhi.pbox.exception.InvalidMessageTypeException;

import com.arrwhi.pbox.msg.flags.Flags;
import com.arrwhi.pbox.msg.metadata.MetaData;
import com.arrwhi.pbox.msg.metadata.MetaDataBuilder;
import com.arrwhi.pbox.util.PathHelper;
import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MessageFactory {
    
    public static final short TRANSPORT_FILE = 0;
    public static final short TRANSPORT_FILE_ACK = 1;
    public static final short DELETE_FILE = 2;
    public static final short DELETE_FILE_ACK = 3;

    private MessageFactory() {}

    public static TransportFileMessage createTransportFileMessageFromBuffer(ByteBuf src) throws InvalidMessageTypeException {
        checkMessageType(MessageFactory.TRANSPORT_FILE, src);
        TransportFileMessage msg = new TransportFileMessage();
        msg.readFrom(src);
        return msg;
    }

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

    public static TransportFileAckMessage createTransportFileAckMessageFromBuffer(ByteBuf src) throws InvalidMessageTypeException {
        checkMessageType(MessageFactory.TRANSPORT_FILE_ACK, src);
        TransportFileAckMessage msg = new TransportFileAckMessage();
        msg.readFrom(src);
        return msg;
    }

    public static TransportFileAckMessage createTransportFileAckMessage(String hash) {
        MetaData metadata = new MetaDataBuilder().withHash(hash).build();
        return new TransportFileAckMessage(metadata);
    }

    public static DeleteFileMessage createDeleteMessageFromBuffer(ByteBuf src) throws InvalidMessageTypeException {
        checkMessageType(MessageFactory.DELETE_FILE, src);
        DeleteFileMessage msg = new DeleteFileMessage();
        msg.readFrom(src);
        return msg;
    }

    public static Message createDeleteFileMessage(File f, String rootDir) {
        String relative = PathHelper.getRelativePath(rootDir, f.toString());
        MetaData metaData = new MetaDataBuilder().withFrom(relative).build();

        return setFlagsOnMessage(
                new DeleteFileMessage(metaData),
                f.isDirectory()
        );
    }

    public static DeleteFileAckMessage createDeleteMessageAckFromBuffer(ByteBuf src) throws InvalidMessageTypeException {
        checkMessageType(MessageFactory.DELETE_FILE_ACK, src);
        DeleteFileAckMessage msg = new DeleteFileAckMessage();
        msg.readFrom(src);
        return msg;
    }

    // TODO...
    public static Message createModifyFileMessage(File f, String rootDir) {
        return null;
    }

    public static String getMessageTypeAsString(short type) {
        switch(type) {
            case MessageFactory.TRANSPORT_FILE:
                return "TRANSPORT_FILE";
            case MessageFactory.TRANSPORT_FILE_ACK:
                return "TRANSPORT_FILE_ACK";
            case MessageFactory.DELETE_FILE:
                return "DELETE_FILE";
            case MessageFactory.DELETE_FILE_ACK:
                return "DELETE_FILE_ACK";
            default:
                return "Unexpected type";
        }
    }

    private static void checkMessageType(short expectedType, ByteBuf src) throws InvalidMessageTypeException {
        short type = src.readShort();
        src.resetReaderIndex();
        if(expectedType != type) {
            String msg = String.format("Invalid message type. Expected %d got %d", expectedType, type);
            throw new InvalidMessageTypeException(msg);
        }
    }

    private static Message setFlagsOnMessage(Message msg, boolean isDirectory) {
        Flags flags = new Flags();
        flags.setIsDirectory(isDirectory);
        msg.setFlags(flags);
        return msg;
    }
}