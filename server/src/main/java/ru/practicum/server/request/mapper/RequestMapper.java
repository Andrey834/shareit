package ru.practicum.server.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.request.dto.RequestDto;
import ru.practicum.server.request.model.Request;
import ru.practicum.server.user.model.User;

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
