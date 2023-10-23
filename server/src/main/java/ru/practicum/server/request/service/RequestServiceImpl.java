package ru.practicum.server.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.request.dto.RequestDto;
import ru.practicum.server.request.exception.RequestNotFoundException;
import ru.practicum.server.request.mapper.RequestMapper;
import ru.practicum.server.request.model.Request;
import ru.practicum.server.request.repository.RequestRepository;
import ru.practicum.server.user.mapper.UserMapper;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

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
    public RequestDto create(long userId, RequestDto requestDto) {
        User user = UserMapper.toUser(userService.get(userId));
        requestDto.setCreated(LocalDateTime.now());
        Request request = RequestMapper.toRequest(requestDto, user);

        Request newRequest = requestRepository.save(request);
        return RequestMapper.toRequestDto(newRequest);
    }

    @Override
    public List<RequestDto> getOwnerRequests(long userId) {
        userService.get(userId);
        return requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getAll(long userId, Pageable pageable) {
        userService.get(userId);
        return requestRepository.findRequestsByRequestorIdIsNot(userId, pageable).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto get(long userId, long requestId) {
        userService.get(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFoundException("Request with ID:" + requestId + "not found")
        );
        return RequestMapper.toRequestDto(request);
    }

    @Override
    public boolean existsById(long requestId) {
        return requestRepository.existsById(requestId);
    }
}
