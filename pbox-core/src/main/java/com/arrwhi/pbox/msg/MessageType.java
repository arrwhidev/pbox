package com.arrwhi.pbox.msg;

import com.arrwhi.pbox.exception.InvalidMessageTypeException;

public enum MessageType {
    TRANSPORT_FILE((short) 0, TransportFileMessage.class),
    TRANSPORT_FILE_ACK((short) 1, TransportFileAckMessage.class),
    DELETE_FILE((short) 2, DeleteFileMessage.class),
    DELETE_FILE_ACK((short) 3, DeleteFileAckMessage.class);

    private short type;
    private Class<? extends Message> clazz;

    MessageType(short type, Class<? extends Message> clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    public short getType() {
        return type;
    }

    public Class<? extends Message> getClazz() {
        return clazz;
    }

    public static MessageType from(short type) throws InvalidMessageTypeException {
        for (MessageType ms : MessageType.values()) {
            if (ms.type == type) {
                return ms;
            }
        }

        throw new InvalidMessageTypeException(type);
    }
}
