package ru.practicum.server.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.exception.UserNotFoundException;
import ru.practicum.server.user.mapper.UserMapper;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User newUser = userRepository.save(user);
        log.info("Create new User with ID:{}", newUser.getId());
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        UserDto oldUserDto = get(userId);
        checkDataForUpdate(userDto, oldUserDto);
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.info("Update User with ID:{}", userId);
        return UserMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto get(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with ID:" + userId + " not found"));
        log.info("Get User with ID:{}", userId);
        return UserMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll() {
        log.info("Get All Users");
        return UserMapper.listToUserDto(userRepository.findAll());
    }

    @Override
    public boolean delete(long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            log.info("Delete User with ID:{}", userId);
            return true;
        }
        return false;
    }

    private void checkDataForUpdate(UserDto updateUser, UserDto oldUserDto) {
        if (updateUser.getName() == null) updateUser.setName(oldUserDto.getName());
        if (updateUser.getEmail() == null) updateUser.setEmail(oldUserDto.getEmail());
        updateUser.setId(oldUserDto.getId());
    }
}
