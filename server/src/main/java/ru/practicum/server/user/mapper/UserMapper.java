package ru.practicum.server.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }

    public static List<UserDto> listToUserDto(List<User> users) {
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
