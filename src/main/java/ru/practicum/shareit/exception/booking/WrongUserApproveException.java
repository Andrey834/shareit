package ru.practicum.shareit.exception.booking;

public class WrongUserApproveException extends RuntimeException {
    public WrongUserApproveException(String message) {
        super(message);
    }
}
