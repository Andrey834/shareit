package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingItemDto {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer bookerId;
}
