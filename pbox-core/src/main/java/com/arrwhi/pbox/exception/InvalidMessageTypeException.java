package com.arrwhi.pbox.exception;

public class InvalidMessageTypeException extends Exception {
    public InvalidMessageTypeException(short type) {
        super("Invalid MessageType: " + type);
    }
}
