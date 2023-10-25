package ru.practicum.server.booking.controller;

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
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.enums.BookingState;
import ru.practicum.server.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> create(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestBody @Valid BookingDto bookingDto
    ) {
        return ResponseEntity.ok(bookingService.save(userId, bookingDto));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> get(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable(name = "bookingId") Integer bookingId
    ) {
        return ResponseEntity.ok(bookingService.get(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAll(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(name = "owner", defaultValue = "false") boolean isOwner
    ) {
        return ResponseEntity.ok(bookingService.getAll(
                userId,
                BookingState.valueOf(state),
                isOwner,
                PageRequest.of(from, size, Sort.by("start").descending())
        ));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable(name = "bookingId") Integer bookingId,
            @RequestParam(name = "approved", defaultValue = "false") boolean approved
    ) {
        return ResponseEntity.ok(bookingService.approveBooking(userId, bookingId, approved));
    }
}
