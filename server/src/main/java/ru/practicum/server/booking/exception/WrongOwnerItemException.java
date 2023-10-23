package ru.practicum.server.booking.exception;

public class WrongOwnerItemException extends RuntimeException {
    public WrongOwnerItemException(String message) {
    super(message);
}
}
