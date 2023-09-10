package ru.practicum.shareit.exception;

public class UserWithoutEmailException extends RuntimeException{
    public UserWithoutEmailException(String message) {
        super(message);
    }
}
