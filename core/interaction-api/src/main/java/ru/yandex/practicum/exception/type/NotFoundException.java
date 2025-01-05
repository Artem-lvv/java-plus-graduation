package ru.yandex.practicum.exception.type;

public class NotFoundException extends RuntimeException {
    public NotFoundException(final String entityType, final long id) {
        super(String.format("Object %s by id - %d not found", entityType, id));
    }

    public NotFoundException(final String message) {
        super(message);
    }
}
