package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class ItemDto {
    @NotEmpty(message = "missing name")
    private String name;
    @NotEmpty(message = "missing description")
    private String description;
    @NotEmpty(message = "missing available")
    private Boolean available;
    private Long requestId;
}
