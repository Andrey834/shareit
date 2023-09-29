package ru.practicum.shareit.exception.booking;

public class BadDataBookingException extends RuntimeException {
    public BadDataBookingException(String message) {
        super(message);
    }
}
