package com.arrwhi.pbox.msg;

import com.arrwhi.pbox.exception.InvalidMessageTypeException;

import io.netty.buffer.ByteBuf;

public class MessageFactory {
    
    public static final short TRANSPORT_FILE = 0;
    public static final short TRANSPORT_FILE_ACK = 1;
    public static final short DELETE_FILE = 2;
    public static final short DELETE_FILE_ACK = 3;

    public static TransportFileMessage createTransportFileMessageFrom(ByteBuf src) throws InvalidMessageTypeException {
        checkMessageType(MessageFactory.TRANSPORT_FILE, src);
        TransportFileMessage msg = new TransportFileMessage();
        msg.readFrom(src);
        return msg;
    }

    public static TransportFileAckMessage createTransportFileAckMessageFrom(ByteBuf src) throws InvalidMessageTypeException {
        checkMessageType(MessageFactory.TRANSPORT_FILE_ACK, src);
        TransportFileAckMessage msg = new TransportFileAckMessage();
        msg.readFrom(src);
        return msg;
    }

    public static DeleteFileMessage createDeleteMessageFrom(ByteBuf src) throws InvalidMessageTypeException {
        checkMessageType(MessageFactory.DELETE_FILE, src);
        DeleteFileMessage msg = new DeleteFileMessage();
        msg.readFrom(src);
        return msg;
    }

    public static DeleteFileAckMessage createDeleteMessageAckFrom(ByteBuf src) throws InvalidMessageTypeException {
        checkMessageType(MessageFactory.DELETE_FILE_ACK, src);
        DeleteFileAckMessage msg = new DeleteFileAckMessage();
        msg.readFrom(src);
        return msg;
    }

    private static void checkMessageType(short expectedType, ByteBuf src) throws InvalidMessageTypeException {
        short type = src.readShort();
        src.resetReaderIndex();
        if(expectedType != type) {
            String msg = String.format("Invalid message type. Expected %d got %d", expectedType, type);
            throw new InvalidMessageTypeException(msg);
        }
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
}