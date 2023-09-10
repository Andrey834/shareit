package ru.practicum.shareit.request;

import lombok.Data;

import java.time.Instant;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private int id;
    private String description;
    private int requestor;
    private Instant created;
}
