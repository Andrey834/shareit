package ru.practicum.shareit.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.exception.model.ErrorResponse;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.exception.user.UserWithoutEmailException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.user.controller.UserController;

@RestControllerAdvice(assignableTypes = {
        UserController.class,
        ItemController.class,
        BookingController.class,
        RequestController.class
})
public class UsersErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserWithoutEmailException(final UserWithoutEmailException e) {
        return new ErrorResponse("error", e.getMessage());
    }
}
