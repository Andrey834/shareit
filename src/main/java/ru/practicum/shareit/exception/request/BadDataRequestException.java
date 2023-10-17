package ru.practicum.shareit.exception.request;

public class BadDataRequestException extends RuntimeException {
    public BadDataRequestException(String message) {
        super(message);
    }
}
