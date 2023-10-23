package ru.practicum.server.booking.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.enums.BookingState;
import ru.practicum.server.booking.service.BookingServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    private BookingServiceImpl bookingService;
    @InjectMocks
    private BookingController bookingController;

    @Test
    void create_whenInvoked_thenResponseStatusOkWithBookingDtoInBody() {
        int userId = 1;
        BookingDto expectedBookingDto = new BookingDto();
        when(bookingService.save(userId, new BookingDto())).thenReturn(expectedBookingDto);

        ResponseEntity<BookingDto> response = bookingController.create(userId, expectedBookingDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBookingDto, response.getBody());
    }

    @Test
    void get_whenInvoked_thenResponseStatusOkWithBookingDtoInBody() {
        int userId = 1;
        int bookingId = 1;
        BookingDto expectedBookingDto = new BookingDto();
        when(bookingService.get(userId, bookingId)).thenReturn(expectedBookingDto);

        ResponseEntity<BookingDto> response = bookingController.get(userId, bookingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBookingDto, response.getBody());
    }

    @Test
    void getAll_whenInvoked_thenResponseStatusOkWithCollectionBookingDtoInBody() {
        int userId = 1;
        String state = "ALL";
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("start").descending());
        List<BookingDto> expectedBookingDtoList = List.of(new BookingDto());
        when(bookingService.getAll(userId, BookingState.ALL, false, pageable))
                .thenReturn(expectedBookingDtoList);

        ResponseEntity<List<BookingDto>> response
                = bookingController.getAll(userId, state, 0, 10, false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBookingDtoList, response.getBody());
    }

    @Test
    void getAllOwner() {
        int userId = 1;
        String state = "ALL";
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("start").descending());
        List<BookingDto> expectedBookingDtoList = List.of(new BookingDto());
        when(bookingService.getAll(userId, BookingState.ALL, true, pageable))
                .thenReturn(expectedBookingDtoList);

        ResponseEntity<List<BookingDto>> response
                = bookingController.getAll(userId, state, 0, 10, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBookingDtoList, response.getBody());
    }

    @Test
    void approveBooking_whenInvoked_thenResponseStatusOkWithBookingDtoInBody() {
        int userId = 1;
        int bookingId = 1;
        BookingDto expectedBookingDto = new BookingDto();
        when(bookingService.approveBooking(userId, bookingId, true))
                .thenReturn(expectedBookingDto);

        ResponseEntity<BookingDto> response = bookingController.approveBooking(userId, bookingId, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBookingDto, response.getBody());
    }
}