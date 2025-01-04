package ru.yandex.practicum.category.exception.type;

public class ConflictDateException extends RuntimeException {
    public ConflictDateException(final String entityType, final long id) {
        super(String.format("Object %s by id - %d is conflict date", entityType, id));
    }

    public ConflictDateException(final String message) {
        super(message);
    }
}
