package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.UserWithoutEmailException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private int countId;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        checkEmail(user);

        final int userId = generatedID();
        user.setId(userId);

        User newUser = userRepository.saveUser(user);
        log.info("Create new User with ID:{}", userId);
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        User updateUser = UserMapper.toUser(userDto);

        checkUserId(userId);
        updateUser.setId(userId);
        checkDataUpdateUser(updateUser);

        log.info("Update User with ID:{}", userId);

        User user = userRepository.updateUser(updateUser);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUser(int userId) {
        checkUserId(userId);
        final User user = userRepository.getUser(userId);
        log.info("Get User with ID:{}", userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        log.info("Get All Users");
        return userRepository.getUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(int userId) {
        checkUserId(userId);
        userRepository.deleteUser(userId);
        log.info("Delete User with ID:{}", userId);
    }

    private int generatedID() {
        return ++countId;
    }

    private void checkEmail(User user) {
        final List<User> userList = userRepository.getUsers();
        final String email = user.getEmail();

        if (email == null || email.isBlank() || email.isEmpty()) {
            log.info("Trying to create a user without email");
            throw new UserWithoutEmailException("User without email");
        }

        Optional<User> findUser = userList.stream()
                .filter(usr -> usr.getId() != user.getId())
                .filter(usr -> usr.getEmail().equals(email))
                .findFirst();

        if (findUser.isPresent()) {
            log.info("Try create User with email: {}, but this email already exists", email);
            throw new DuplicateEmailException(email + " already exists");
        }
    }

    private void checkDataUpdateUser(User user) {
        User oldUser = userRepository.getUser(user.getId());

        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }

        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        } else {
            checkEmail(user);
        }
    }

    private void checkUserId(int userId) {
        User user = userRepository.getUser(userId);
        if (user == null) {
            log.info("User with ID:{} not found", userId);
            throw new UserNotFoundException("User with ID:" + userId + " not found");
        }
    }
}
