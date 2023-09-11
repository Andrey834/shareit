package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(int userId, ItemDto itemDto);

    ItemDto update(int userId, ItemDto itemDto, int itemId);

    ItemDto get(int userId, int itemId);

    List<ItemDto> getAll(int userId);

    List<ItemDto> search(int userId, String strSearch);
}
