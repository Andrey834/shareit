package ru.practicum.server.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.server.request.dto.RequestDto;
import ru.practicum.server.request.service.RequestService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestDto> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody RequestDto requestDto
    ) {
        return ResponseEntity.ok(requestService.create(userId, requestDto));
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> getOwnerRequest(
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        return ResponseEntity.ok(requestService.getOwnerRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDto>> getAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return ResponseEntity.ok(requestService.getAll(userId, PageRequest.of(
                from,
                size,
                Sort.by("created").descending())
        ));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDto> get(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable(name = "requestId") Integer requestId
    ) {
        return ResponseEntity.ok(requestService.get(userId, requestId));
    }
}
