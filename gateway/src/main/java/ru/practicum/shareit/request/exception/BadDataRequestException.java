package ru.practicum.shareit.request.exception;

public class BadDataRequestException extends RuntimeException {
    public BadDataRequestException(String message) {
        super(message);
    }
}
