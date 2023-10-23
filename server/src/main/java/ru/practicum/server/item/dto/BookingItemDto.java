package ru.practicum.server.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingItemDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long bookerId;
}
