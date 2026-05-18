package ru.practicum.server.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.enums.BookingStatus;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    private User buildUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("User" + id);
        user.setEmail("user" + id + "@example.com");
        return user;
    }

    private Item buildItem(long id, User owner) {
        Item item = new Item();
        item.setId(id);
        item.setName("Item" + id);
        item.setDescription("Description" + id);
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    @Test
    void toBookingDto_mapsAllFields() {
        User owner = buildUser(1L);
        User booker = buildUser(2L);
        Item item = buildItem(10L, owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Booking booking = new Booking();
        booking.setId(7L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto dto = BookingMapper.toBookingDto(booking);

        assertEquals(7L, dto.getId());
        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
        assertEquals(10L, dto.getItemId());
        assertEquals(2L, dto.getBooker().getId());
        assertEquals(BookingStatus.WAITING, dto.getStatus());
    }

    @Test
    void toBooking_mapsAllFields() {
        User owner = buildUser(1L);
        User booker = buildUser(2L);
        Item item = buildItem(10L, owner);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Booking source = new Booking();
        source.setId(7L);
        source.setStart(start);
        source.setEnd(end);
        source.setItem(item);
        source.setBooker(booker);
        source.setStatus(BookingStatus.APPROVED);

        BookingDto dto = BookingMapper.toBookingDto(source);
        Booking booking = BookingMapper.toBooking(dto);

        assertEquals(7L, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(10L, booking.getItem().getId());
        assertEquals(2L, booking.getBooker().getId());
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void toBookingDto_thenToBooking_isSymmetric() {
        User owner = buildUser(1L);
        User booker = buildUser(3L);
        Item item = buildItem(5L, owner);

        Booking original = new Booking();
        original.setId(1L);
        original.setStart(LocalDateTime.now().plusHours(1));
        original.setEnd(LocalDateTime.now().plusHours(3));
        original.setItem(item);
        original.setBooker(booker);
        original.setStatus(BookingStatus.REJECTED);

        BookingDto dto = BookingMapper.toBookingDto(original);
        Booking roundTripped = BookingMapper.toBooking(dto);

        assertEquals(original.getId(), roundTripped.getId());
        assertEquals(original.getStart(), roundTripped.getStart());
        assertEquals(original.getEnd(), roundTripped.getEnd());
        assertEquals(original.getStatus(), roundTripped.getStatus());
    }
}
