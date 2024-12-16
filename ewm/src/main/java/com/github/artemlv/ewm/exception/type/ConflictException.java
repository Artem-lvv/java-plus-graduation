package com.github.artemlv.ewm.exception.type;

public class ConflictException extends RuntimeException {
    public ConflictException(final String entityType, final long id) {
        super(String.format("Object %s by id - %d is conflict", entityType, id));
    }

    public ConflictException(final String message) {
        super(message);
    }
}
