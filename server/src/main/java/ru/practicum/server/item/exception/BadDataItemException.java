package ru.practicum.server.item.exception;

public class BadDataItemException extends RuntimeException {
    public BadDataItemException(String message) {
        super(message);
    }
}
