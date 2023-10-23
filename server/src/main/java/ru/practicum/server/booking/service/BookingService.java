package ru.practicum.server.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.enums.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto save(long userId, BookingDto bookingDto);

    BookingDto get(long userId, long bookingId);

    List<BookingDto> getAll(long userId, BookingState state, boolean isOwner, Pageable pageable);

    BookingDto approveBooking(long userId, long bookingId, boolean approved);
}
