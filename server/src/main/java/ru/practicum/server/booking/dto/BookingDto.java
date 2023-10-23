package ru.practicum.server.booking.dto;

import lombok.Data;
import ru.practicum.server.booking.enums.BookingStatus;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;
    private ItemDto item;
    private UserDto booker;
    private BookingStatus status;
}
