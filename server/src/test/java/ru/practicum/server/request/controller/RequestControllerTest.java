package ru.practicum.server.request.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.server.request.dto.RequestDto;
import ru.practicum.server.request.service.RequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {
    @Mock
    private RequestService requestService;
    @InjectMocks
    private RequestController requestController;

    @Test
    void create_whenInvoked_thenResponseStatusOkWithRequestInBody() {
        int userId = 1;
        RequestDto expectedRequestDto = new RequestDto();
        when(requestService.create(userId, new RequestDto())).thenReturn(expectedRequestDto);

        ResponseEntity<RequestDto> response = requestController.create(userId, expectedRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRequestDto, response.getBody());
    }

    @Test
    void getOwnerRequest_whenInvoked_thenResponseStatusOkWithRequestListInBody() {
        int userId = 1;
        List<RequestDto> expectedRequestDtoList = List.of(new RequestDto());
        when(requestService.getOwnerRequests(userId)).thenReturn(expectedRequestDtoList);

        ResponseEntity<List<RequestDto>> response = requestController.getOwnerRequest(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRequestDtoList, response.getBody());
    }

    @Test
    void getAll_whenInvoked_thenResponseStatusOkWithRequestListInBody() {
        int userId = 1;
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("created").descending());
        List<RequestDto> expectedRequestDtoList = List.of(new RequestDto());
        when(requestService.getAll(userId, pageable)).thenReturn(expectedRequestDtoList);

        ResponseEntity<List<RequestDto>> response = requestController.getAll(userId, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRequestDtoList, response.getBody());
    }

    @Test
    void get_whenInvoked_thenResponseStatusOkWithRequestInBody() {
        int userId = 1;
        int requestId = 1;
        RequestDto expectedRequestDto = new RequestDto();
        when(requestService.get(userId, requestId)).thenReturn(expectedRequestDto);

        ResponseEntity<RequestDto> response = requestController.get(userId, requestId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedRequestDto, response.getBody());
    }
}