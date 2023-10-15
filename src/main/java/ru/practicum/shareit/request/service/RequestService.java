package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto create(Integer userId, RequestDto requestDto);

    List<RequestDto> getOwnerRequests(Integer userId);

    List<RequestDto> getAll(Integer userId, Pageable pageable);

    RequestDto get(Integer userId, Integer requestId);

    boolean existsById(Integer requestId);
}
