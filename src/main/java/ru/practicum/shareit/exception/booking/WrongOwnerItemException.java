package ru.practicum.shareit.exception.booking;

public class WrongOwnerItemException extends RuntimeException {
    public WrongOwnerItemException(String message) {
    super(message);
}
}
