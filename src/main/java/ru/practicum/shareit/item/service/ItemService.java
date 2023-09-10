package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(int userId, ItemDto itemDto);

    ItemDto updateItem(int userId, ItemDto itemDto, int itemId);

    ItemDto getItem(int userId, int itemId);

    List<ItemDto> getItems(int userId);

    List<ItemDto> searchItem(int userId, String strSearch);
}
