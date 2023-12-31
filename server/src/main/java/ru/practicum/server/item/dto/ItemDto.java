package ru.practicum.server.item.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingItemDto nextBooking;
    private BookingItemDto lastBooking;
    private List<CommentDto> comments = new ArrayList<>();
    private long requestId;
}
