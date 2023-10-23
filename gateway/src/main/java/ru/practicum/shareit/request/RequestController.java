package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.exception.BadDataRequestException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody RequestDto requestDto
    ) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isEmpty()) {
            throw new BadDataRequestException("empty description");
        }
        log.info("***User ID: {} create Request: {}", userId, requestDto);
        return requestClient.create(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable(name = "requestId") Integer requestId
    ) {
        log.info("***User ID: {} get Request ID: {}", userId, requestId);
        return requestClient.get(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerRequest(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("***User ID: {} get OwnerRequest", userId);
        return requestClient.getOwnerRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("***User ID: {} get all requests", userId);
        return requestClient.getAll(userId, from, size);
    }
}
