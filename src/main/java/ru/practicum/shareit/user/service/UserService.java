package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto UserDto);

    UserDto updateUser(int userId, UserDto userDto);

    UserDto getUser(int userId);

    List<UserDto> getUsers();

    void deleteUser(int userId);
}
