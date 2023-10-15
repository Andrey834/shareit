package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(int userId, UserDto userDto);

    UserDto get(int userId);

    List<UserDto> getAll();

    boolean delete(int userId);
}
