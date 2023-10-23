package ru.practicum.server.request.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.server.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = "created")
public class RequestDto {
    private long id;
    private String description;
    private long requestor;
    private LocalDateTime created;
    private List<ItemDto> items = new ArrayList<>();
}
