package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemDto create(int userId, ItemDto itemDto);

    ItemDto update(int userId, ItemDto itemDto, int itemId);

    ItemDto get(Integer userId, Integer itemId);

    List<ItemDto> getAll(int userId);

    List<ItemDto> search(int userId, String strSearch);

    CommentDto addComment(Integer itemId, Integer userId, Comment comment);
}
