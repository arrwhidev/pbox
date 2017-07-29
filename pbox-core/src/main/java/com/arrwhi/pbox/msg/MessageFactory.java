package com.arrwhi.pbox.msg;

import com.arrwhi.pbox.crypto.HashFactory;
import com.arrwhi.pbox.exception.InvalidMessageTypeException;

import com.arrwhi.pbox.json.MetaData;
import com.arrwhi.pbox.msg.flags.Flags;
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

    public static TransportFileMessage createTransportFileMessageFromBuffer(ByteBuf src) throws InvalidMessageTypeException {
        checkMessageType(MessageFactory.TRANSPORT_FILE, src);
        TransportFileMessage msg = new TransportFileMessage();
        msg.readFrom(src);
        return msg;
    }

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

    public static TransportFileAckMessage createTransportFileAckMessageFromBuffer(ByteBuf src) throws InvalidMessageTypeException {
        checkMessageType(MessageFactory.TRANSPORT_FILE_ACK, src);
        TransportFileAckMessage msg = new TransportFileAckMessage();
        msg.readFrom(src);
        return msg;
    }

    public static TransportFileAckMessage createTransportFileAckMessage(String hash) {
        MetaData metadata = new MetaData();
        metadata.setHash(hash);
        return new TransportFileAckMessage(MetaData.toJsonBytes(metadata));
    }

    public static DeleteFileMessage createDeleteMessageFromBuffer(ByteBuf src) throws InvalidMessageTypeException {
        checkMessageType(MessageFactory.DELETE_FILE, src);
        DeleteFileMessage msg = new DeleteFileMessage();
        msg.readFrom(src);
        return msg;
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