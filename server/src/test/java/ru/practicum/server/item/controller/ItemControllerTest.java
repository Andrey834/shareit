package ru.practicum.server.item.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;

    @Test
    void create_whenInvoked_thenResponseStatusOkWithItemInBody() {
        long userId = 1;
        ItemDto expectedItemDto = new ItemDto();
        when(itemService.create(userId, new ItemDto()))
                .thenReturn(expectedItemDto);

        ResponseEntity<ItemDto> response = itemController.create(userId, expectedItemDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItemDto, response.getBody());
    }

    @Test
    void update_whenInvoked_thenResponseStatusOkWithItemInBody() {
        long userId = 1;
        long itemId = 1;
        ItemDto expectedItemDto = new ItemDto();
        when(itemService.update(userId, new ItemDto(), itemId))
                .thenReturn(expectedItemDto);

        ResponseEntity<ItemDto> response = itemController.update(userId, expectedItemDto, itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItemDto, response.getBody());
    }

    @Test
    void get_whenInvoked_thenResponseStatusOkWithItemInBody() {
        long userId = 1;
        long itemId = 1;
        ItemDto expectedItemDto = new ItemDto();
        when(itemService.get(userId, itemId)).thenReturn(expectedItemDto);

        ResponseEntity<ItemDto> response = itemController.get(userId, itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItemDto, response.getBody());
    }

    @Test
    void addComment_whenInvoked_thenResponseStatusOkWithCommentInBody() {
        long userId = 1;
        long itemId = 1;
        CommentDto expectedCommentDto = new CommentDto();
        when(itemService.addComment(itemId, userId, new Comment()))
                .thenReturn(expectedCommentDto);

        ResponseEntity<CommentDto> response = itemController
                .addComment(itemId, userId, new Comment());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCommentDto, response.getBody());
    }

    @Test
    void getAll() {
        long userId = 1;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("id"));
        List<ItemDto> expectedItemDtoList = List.of(new ItemDto());
        Mockito.when(itemService.getAll(userId, pageable)).thenReturn(expectedItemDtoList);

        ResponseEntity<List<ItemDto>> response = itemController.getAll(userId, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItemDtoList, response.getBody());
    }

    @Test
    void search() {
        long userId = 1;
        String text = "anything";
        PageRequest pageable = PageRequest.of(0, 10);
        List<ItemDto> expectedItemDtoList = List.of(new ItemDto());
        Mockito.when(itemService.search(userId, text, pageable))
                .thenReturn(expectedItemDtoList);

        ResponseEntity<List<ItemDto>> response = itemController.search(userId, text, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedItemDtoList, response.getBody());
    }
}