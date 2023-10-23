package ru.practicum.server.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.server.booking.controller.BookingController;
import ru.practicum.server.booking.exception.BookingNotFoundException;
import ru.practicum.server.booking.exception.NoAccessBookingException;
import ru.practicum.server.booking.exception.UnsupportedStatusException;
import ru.practicum.server.booking.exception.WrongOwnerItemException;
import ru.practicum.server.booking.exception.WrongUserApproveException;
import ru.practicum.server.exception.model.ErrorResponse;
import ru.practicum.server.item.controller.ItemController;
import ru.practicum.server.item.exception.BadDataItemException;
import ru.practicum.server.item.exception.ItemNotAvailableException;
import ru.practicum.server.item.exception.ItemNotFoundException;
import ru.practicum.server.request.controller.RequestController;
import ru.practicum.server.request.exception.RequestNotFoundException;
import ru.practicum.server.user.controller.UserController;
import ru.practicum.server.user.exception.UserNotFoundException;

@RestControllerAdvice(assignableTypes = {
        UserController.class,
        ItemController.class,
        BookingController.class,
        RequestController.class
})
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRequestNotFoundException(final RequestNotFoundException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundItemException(final ItemNotFoundException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWrongOwnerItemException(final WrongOwnerItemException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotAvailableItemException(final ItemNotAvailableException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadDataItemException(final BadDataItemException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedStatusBookingException(final UnsupportedStatusException e) {
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotFoundException(final BookingNotFoundException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoAccessBookingException(final NoAccessBookingException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleWrongUserApproveException(final WrongUserApproveException e) {
        return new ErrorResponse("error", e.getMessage());
    }
}
