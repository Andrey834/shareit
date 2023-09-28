package ru.practicum.shareit.exception.booking;

public class NoAccessBookingException extends RuntimeException {
    public NoAccessBookingException(String message) {
        super(message);
    }
}
