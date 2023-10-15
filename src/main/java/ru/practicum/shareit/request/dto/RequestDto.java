package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = "created")
public class RequestDto {
    private Integer id;
    private String description;
    private Integer requestor;
    private LocalDateTime created;
    private List<ItemDto> items = new ArrayList<>();
}
