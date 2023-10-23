package ru.practicum.server.booking.exception;

public class NoAccessBookingException extends RuntimeException {
    public NoAccessBookingException(String message) {
        super(message);
    }
}
