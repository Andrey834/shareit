package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.exception.user.UserWithoutEmailException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ru.practicum.shareit.user.dto.UserDto create(ru.practicum.shareit.user.dto.UserDto userDto) {
        if (userDto.getEmail() == null) throw new UserWithoutEmailException("without email");
        User user = UserMapper.toUser(userDto);

        User newUser = userRepository.save(user);
        log.info("Create new User with ID:{}", newUser.getId());
        return UserMapper.toUserDto(newUser);
    }

    @Transactional
    @Override
    public UserDto update(int userId, ru.practicum.shareit.user.dto.UserDto userDto) {
        UserDto oldUserDto = get(userId);
        checkDataForUpdate(userDto, oldUserDto);
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.info("Update User with ID:{}", userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public ru.practicum.shareit.user.dto.UserDto get(int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with ID:" + userId + " not found"));
        log.info("Get User with ID:{}", userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<ru.practicum.shareit.user.dto.UserDto> getAll() {
        log.info("Get All Users");
        return UserMapper.listToUserDto(userRepository.findAll());
    }

    @Transactional
    @Override
    public boolean delete(int userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            log.info("Delete User with ID:{}", userId);
            return true;
        }
        return false;
    }

    private void checkDataForUpdate(ru.practicum.shareit.user.dto.UserDto updateUser, ru.practicum.shareit.user.dto.UserDto oldUserDto) {
        if (updateUser.getName() == null) updateUser.setName(oldUserDto.getName());
        if (updateUser.getEmail() == null) updateUser.setEmail(oldUserDto.getEmail());
        updateUser.setId(oldUserDto.getId());
    }
}
