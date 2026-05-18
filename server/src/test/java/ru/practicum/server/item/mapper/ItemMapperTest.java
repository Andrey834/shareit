package ru.practicum.server.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.enums.BookingStatus;
import ru.practicum.server.item.dto.BookingItemDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemMapperTest {

    private Item buildItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setRequestId(10L);
        return item;
    }

    @Test
    void toItemDto_mapsAllFields() {
        Item item = buildItem();

        ItemDto dto = ItemMapper.toItemDto(item);

        assertEquals(1L, dto.getId());
        assertEquals("Drill", dto.getName());
        assertEquals("Powerful drill", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(10L, dto.getRequestId());
    }

    @Test
    void toItem_mapsAllFields() {
        ItemDto dto = new ItemDto();
        dto.setId(2L);
        dto.setName("Hammer");
        dto.setDescription("Heavy hammer");
        dto.setAvailable(false);

        Item item = ItemMapper.toItem(dto);

        assertEquals(2L, item.getId());
        assertEquals("Hammer", item.getName());
        assertEquals("Heavy hammer", item.getDescription());
        assertFalse(item.isAvailable());
    }

    @Test
    void listToItemDto_mapsAllElements() {
        Item i1 = buildItem();
        Item i2 = new Item();
        i2.setId(2L);
        i2.setName("Saw");
        i2.setDescription("Hand saw");
        i2.setAvailable(false);

        List<ItemDto> result = ItemMapper.listToItemDto(List.of(i1, i2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Drill", result.get(0).getName());
        assertEquals("Saw", result.get(1).getName());
    }

    @Test
    void toBookingItemDto_mapsAllFields() {
        User booker = new User();
        booker.setId(42L);

        Item item = buildItem();

        Booking booking = new Booking();
        booking.setId(7L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        booking.setStart(start);
        booking.setEnd(end);

        BookingItemDto dto = ItemMapper.toBookingItemDto(booking);

        assertEquals(7L, dto.getId());
        assertEquals(42L, dto.getBookerId());
        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
    }
}
