package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.exception.BadDataBookingException;
import ru.practicum.shareit.booking.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.model.ErrorResponse;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.exception.BadDataItemException;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.exception.BadDataRequestException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.exception.UserWithoutEmailException;

@RestControllerAdvice(assignableTypes = {
        BookingController.class,
        UserController.class,
        RequestController.class,
        ItemController.class})
public class ErrorHandler {

    @ExceptionHandler({UnsupportedStatusException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedStatusBookingException(final UnsupportedStatusException e) {
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadDataBookingException(final BadDataBookingException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserWithoutEmailException(final UserWithoutEmailException e) {
        return new ErrorResponse("error", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadDataRequestException(final BadDataRequestException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadDataItemException(final BadDataItemException e) {
        return new ErrorResponse("error", e.getMessage());
    }
}
