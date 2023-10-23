package ru.practicum.server.exception.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private final String error;
    private final String description;
}
