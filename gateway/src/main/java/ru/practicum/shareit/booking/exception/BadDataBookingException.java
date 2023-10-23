package ru.practicum.shareit.booking.exception;

public class BadDataBookingException extends RuntimeException {
    public BadDataBookingException(String message) {
        super(message);
    }
}
