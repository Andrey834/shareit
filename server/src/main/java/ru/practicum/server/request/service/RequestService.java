package ru.practicum.server.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.server.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(long userId, RequestDto requestDto);

    List<RequestDto> getOwnerRequests(long userId);

    List<RequestDto> getAll(long userId, Pageable pageable);

    RequestDto get(long userId, long requestId);

    boolean existsById(long requestId);
}
