package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadDataItemException;
import ru.practicum.shareit.exception.WrongOwnerItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private int count = 0;

    @Override
    public ItemDto createItem(int userId, ItemDto itemDto) {
        checkDataItem(itemDto);
        userService.getUser(userId);
        generatedId(itemDto);
        Item item = ItemMapper.toItem(userId, itemDto);
        return ItemMapper.toItemDto(itemRepository.saveItem(item));
    }

    @Override
    public ItemDto updateItem(int userId, ItemDto itemDto, int itemId) {
        userService.getUser(userId);
        checkOwnerItem(itemId, userId);

        ItemDto oldItemDto = getItem(userId, itemId);
        changeItemData(itemDto, oldItemDto);

        Item item = ItemMapper.toItem(userId, itemDto);

        return ItemMapper.toItemDto(itemRepository.updateItem(item));
    }

    @Override
    public ItemDto getItem(int userId, int itemId) {
        userService.getUser(userId);
        return ItemMapper.toItemDto(itemRepository.getItem(itemId));
    }

    @Override
    public List<ItemDto> getItems(int userId) {
        return itemRepository.getItems()
                .stream()
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(int userId, String strSearch) {
        userService.getUser(userId);
        String str = strSearch.toLowerCase();
        if (str.isBlank()) return new ArrayList<>();

        return itemRepository.getItems()
                .stream()
                .filter(item ->
                        item.getDescription().toLowerCase().contains(str)
                        || item.getName().toLowerCase().contains(str)
                )
                .filter(Item::isAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void generatedId(ItemDto itemDto) {
        itemDto.setId(++count);
    }

    private void checkDataItem(ItemDto itemDto) {
        final String itemName = itemDto.getName();
        final String itemDesc = itemDto.getDescription();
        final Boolean itemAvailable = itemDto.getAvailable();

        if (itemName == null || itemName.isBlank() || itemName.isEmpty()) {
            throw new BadDataItemException("item name not specified");
        } else if (itemDesc == null || itemDesc.isBlank() || itemDesc.isEmpty()) {
            throw new BadDataItemException("item description not specified");
        } else if (itemAvailable == null) {
            throw new BadDataItemException("item available not specified");
        }
    }

    private void changeItemData(ItemDto itemDto, ItemDto oldItemDto) {
        itemDto.setId(oldItemDto.getId());

        if (itemDto.getName() == null) itemDto.setName(oldItemDto.getName());
        if (itemDto.getDescription() == null) itemDto.setDescription(oldItemDto.getDescription());
        if (itemDto.getAvailable() == null) itemDto.setAvailable(oldItemDto.getAvailable());
    }

    private void checkOwnerItem(int itemId, int userId) {
        Item item = itemRepository.getItem(itemId);
        if (item.getOwner() != userId) {
            throw new WrongOwnerItemException("User with ID:" + userId + " is not the owner");
        }
    }
}
