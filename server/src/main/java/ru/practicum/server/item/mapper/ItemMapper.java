package ru.practicum.server.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.item.dto.BookingItemDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        itemDto.setRequestId(item.getRequestId());

        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());

        return item;
    }

    public static List<ItemDto> listToItemDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        BookingItemDto bookingItemDto = new BookingItemDto();
        bookingItemDto.setId(booking.getId());
        bookingItemDto.setStart(booking.getStart());
        bookingItemDto.setEnd(booking.getEnd());
        bookingItemDto.setBookerId(booking.getBooker().getId());

        return bookingItemDto;
    }
}
