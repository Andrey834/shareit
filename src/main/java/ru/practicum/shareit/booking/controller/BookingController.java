package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestBody @Valid BookingDto bookingDto
    ) {
        return bookingService.save(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable(name = "bookingId") Integer bookingId
    ) {
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAll(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state
    ) {
        return bookingService.getAll(userId, state, false);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwner(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state
    ) {
        return bookingService.getAll(userId, state, true);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable(name = "bookingId") Integer bookingId,
            @RequestParam(name = "approved", defaultValue = "false") boolean approved
    ) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }
}
