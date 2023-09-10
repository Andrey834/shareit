package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.Instant;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private int id;
    private Instant start;
    private Instant end;
    private int item;
    private int booker;
    private BookingStatus status;
}
