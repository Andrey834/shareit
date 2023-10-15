package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper {
    public static Request toRequest(RequestDto requestDto, User user) {
        Request request = new Request();
        request.setId(request.getId());
        request.setDescription(requestDto.getDescription());
        request.setCreated(requestDto.getCreated());
        request.setRequestor(user);

        return request;
    }

    public static RequestDto toRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setRequestor(request.getRequestor().getId());
        requestDto.setCreated(request.getCreated());
        List<ItemDto> items = request.getItems().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        requestDto.setItems(items);

        return requestDto;
    }
}
