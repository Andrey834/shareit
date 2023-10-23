package ru.practicum.server.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemDto itemDto
    ) {
        return ResponseEntity.ok(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemDto itemDto,
            @PathVariable long itemId
    ) {
        return ResponseEntity.ok(itemService.update(userId, itemDto, itemId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> get(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable(value = "itemId") long itemId
    ) {
        return ResponseEntity.ok(itemService.get(userId, itemId));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable(value = "itemId") long itemId,
            @RequestBody @Valid Comment comment
    ) {
        return ResponseEntity.ok(itemService.addComment(itemId, userId, comment));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return ResponseEntity.ok(itemService.getAll(
                userId,
                PageRequest.of(from, size, Sort.by("id")))
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "text") String strSearch,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(itemService.search(
                userId,
                strSearch,
                PageRequest.of(from, size))
        );
    }
}
