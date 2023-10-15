package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.request.BadDataRequestException;
import ru.practicum.shareit.exception.request.RequestNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;

    @Transactional
    @Override
    public RequestDto create(Integer userId, RequestDto requestDto) {
        String text = requestDto.getDescription();
        if (text == null || text.isEmpty()) throw new BadDataRequestException("empty description");

        User user = UserMapper.toUser(userService.get(userId));
        requestDto.setCreated(LocalDateTime.now());
        Request request = RequestMapper.toRequest(requestDto, user);

        Request newRequest = requestRepository.save(request);
        return RequestMapper.toRequestDto(newRequest);
    }

    @Override
    public List<RequestDto> getOwnerRequests(Integer userId) {
        userService.get(userId);
        return requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getAll(Integer userId, Pageable pageable) {
        userService.get(userId);
        return requestRepository.findRequestsByRequestorIdIsNot(userId, pageable).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto get(Integer userId, Integer requestId) {
        userService.get(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFoundException("Request with ID:" + requestId + "not found")
        );
        return RequestMapper.toRequestDto(request);
    }

    @Override
    public boolean existsById(Integer requestId) {
        return requestRepository.existsById(requestId);
    }
}
