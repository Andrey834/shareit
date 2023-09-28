package ru.practicum.shareit.request;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    private int id;
    private String description;
    private int requestor;
    private Instant created;
}
