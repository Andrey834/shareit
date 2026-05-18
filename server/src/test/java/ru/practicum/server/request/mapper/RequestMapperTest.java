package ru.practicum.server.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.request.dto.RequestDto;
import ru.practicum.server.request.model.Request;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RequestMapperTest {

    private User buildUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("User" + id);
        user.setEmail("user" + id + "@example.com");
        return user;
    }

    @Test
    void toRequest_mapsDescriptionAndRequestor() {
        User user = buildUser(1L);

        RequestDto dto = new RequestDto();
        dto.setDescription("Need a drill");
        dto.setCreated(LocalDateTime.now());

        Request request = RequestMapper.toRequest(dto, user);

        assertEquals("Need a drill", request.getDescription());
        assertEquals(user, request.getRequestor());
    }

    @Test
    void toRequestDto_mapsAllFields() {
        User user = buildUser(2L);

        Item item = new Item();
        item.setId(10L);
        item.setName("Drill");
        item.setDescription("desc");
        item.setAvailable(true);

        LocalDateTime created = LocalDateTime.now();

        Request request = new Request();
        request.setId(5L);
        request.setDescription("Need a drill");
        request.setRequestor(user);
        request.setCreated(created);
        request.setItems(List.of(item));

        RequestDto dto = RequestMapper.toRequestDto(request);

        assertEquals(5L, dto.getId());
        assertEquals("Need a drill", dto.getDescription());
        assertEquals(2L, dto.getRequestor());
        assertEquals(created, dto.getCreated());
        assertNotNull(dto.getItems());
        assertEquals(1, dto.getItems().size());
        assertEquals("Drill", dto.getItems().get(0).getName());
    }

    @Test
    void toRequestDto_withNoItems_returnsEmptyList() {
        User user = buildUser(3L);

        Request request = new Request();
        request.setId(1L);
        request.setDescription("Need something");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        RequestDto dto = RequestMapper.toRequestDto(request);

        assertNotNull(dto.getItems());
        assertEquals(0, dto.getItems().size());
    }
}
