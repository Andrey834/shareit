package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.request.BadDataRequestException;
import ru.practicum.shareit.exception.request.RequestNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserServiceImpl userService;
    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void create_whenValidData_thenReturnedRequestDto() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);

        Request request = new Request();
        request.setRequestor(user);
        request.setDescription("Что-то дайте");
        request.setCreated(LocalDateTime.now());
        RequestDto expectedRequestDto = RequestMapper.toRequestDto(request);

        when(userService.get(userId))
                .thenReturn(UserMapper.toUserDto(user));

        when(requestRepository.save(request))
                .thenReturn(request);

        RequestDto actualRequestDto = requestService.create(userId, expectedRequestDto);

        verify(requestRepository, times(1))
                .save(request);
        assertEquals(expectedRequestDto, actualRequestDto);
    }

    @Test
    void create_whenTextNullOrEmpty_thenBadDataRequestExceptionThrow() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);

        Request request = new Request();
        request.setRequestor(user);
        RequestDto expectedRequestDto = RequestMapper.toRequestDto(request);

        assertThrows(BadDataRequestException.class,
                () -> requestService.create(userId, expectedRequestDto));
    }

    @Test
    void getOwnerRequests() {
        int userId = 1;
        User user = new User();
        user.setId(userId);
        Request request = new Request();
        request.setId(1);
        request.setDescription("wow");
        request.setRequestor(user);
        List<Request> requestsList = List.of(request);
        List<RequestDto> expectedList = requestsList.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());

        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId))
                .thenReturn(requestsList);

        List<RequestDto> actualList = requestService.getOwnerRequests(userId);

        verify(requestRepository, times(1))
                .findAllByRequestorIdOrderByCreatedDesc(userId);

        assertEquals(expectedList, actualList);
    }

    @Test
    void getAll() {
        PageRequest pageable = PageRequest.of(0, 20);
        int userId = 1;
        User user = new User();
        user.setId(userId);
        Request request = new Request();
        request.setId(1);
        request.setDescription("wow");
        request.setRequestor(user);
        List<Request> requestsList = List.of(request);
        Page<Request> page = new PageImpl<>(requestsList);
        List<RequestDto> expectedList = requestsList.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());

        when(requestRepository.findRequestsByRequestorIdIsNot(userId, pageable)).thenReturn(page);

        List<RequestDto> actualList = requestService.getAll(userId, pageable);

        verify(requestRepository, times(1))
                .findRequestsByRequestorIdIsNot(userId, pageable);

        assertEquals(expectedList, actualList);
    }

    @Test
    void get_whenUserIdAndRequestIdValid_thenReturnedRequestDto() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);

        int requestId = 1;
        Request request = new Request();
        request.setId(requestId);
        request.setRequestor(user);
        request.setDescription("Что-то дайте");
        request.setCreated(LocalDateTime.now());
        RequestDto expectedRequestDto = RequestMapper.toRequestDto(request);

        when(userService.get(userId)).thenReturn(UserMapper.toUserDto(user));

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        RequestDto actualRequestDto = requestService.get(userId, requestId);

        verify(requestRepository, times(1)).findById(requestId);
        assertEquals(expectedRequestDto, actualRequestDto);

    }

    @Test
    void get_whenRequestIdNotValid_thenRequestNotFoundExceptionThrow() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);
        int wrongRequestId = 100;

        when(userService.get(userId)).thenReturn(UserMapper.toUserDto(user));

        when(requestRepository.findById(wrongRequestId)).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class,
                () -> requestService.get(userId, wrongRequestId));
    }

    @Test
    void existsById_whenRequestFound_thenReturnedTrue() {
        int requestId = 1;

        when(requestRepository.existsById(requestId)).thenReturn(true);

        boolean expectedAnswer = requestService.existsById(requestId);
        assertTrue(expectedAnswer);
    }
}