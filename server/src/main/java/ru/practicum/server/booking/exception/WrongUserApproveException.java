package ru.practicum.server.booking.exception;

public class WrongUserApproveException extends RuntimeException {
    public WrongUserApproveException(String message) {
        super(message);
    }
}
