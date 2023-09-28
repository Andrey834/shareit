package ru.practicum.shareit.exception.user;

public class UserWithoutEmailException extends RuntimeException {
    public UserWithoutEmailException(String message) {
        super(message);
    }
}
