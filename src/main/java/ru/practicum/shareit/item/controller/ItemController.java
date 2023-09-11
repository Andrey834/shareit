package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestBody ItemDto itemDto
    ) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestBody ItemDto itemDto,
            @PathVariable int itemId
    ) {
        return itemService.update(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @PathVariable(value = "itemId") int itemId
    ) {
        return itemService.get(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getAll(
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestHeader("X-Sharer-User-Id") int userId,
            @RequestParam(value = "text") String strSearch
    ) {
        return itemService.search(userId, strSearch);
    }
}
