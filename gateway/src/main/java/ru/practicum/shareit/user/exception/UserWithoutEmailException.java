package ru.practicum.shareit.user.exception;

public class UserWithoutEmailException extends RuntimeException {
    public UserWithoutEmailException(String message) {
        super(message);
    }
}
