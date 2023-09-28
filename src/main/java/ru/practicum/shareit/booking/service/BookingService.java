package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto save(Integer userId, BookingDto bookingDto);

    BookingDto get(Integer userId, Integer bookingId);

    List<BookingDto> getAll(Integer userId, String state, boolean isOwner);

    BookingDto approveBooking(Integer userId, Integer bookingId, boolean approved);
}