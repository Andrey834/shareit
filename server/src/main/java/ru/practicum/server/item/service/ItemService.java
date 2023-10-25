package ru.practicum.server.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, ItemDto itemDto, long itemId);

    ItemDto get(long userId, long itemId);

    List<ItemDto> getAll(long userId, Pageable pageable);

    List<ItemDto> search(long userId, String strSearch, Pageable pageable);

    CommentDto addComment(long itemId, long userId, Comment comment);

    Item getAvailableItem(long itemId);
}
