package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.user.DuplicateEmailException;
import ru.practicum.shareit.exception.user.UserNotFoundException;
import ru.practicum.shareit.exception.user.UserWithoutEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void create_whenEmailValid_thenSavedUser() {
        var userToSave = new User();
        var validEmail = "test@test.ru";
        userToSave.setEmail(validEmail);

        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserDto userDtoToSave = UserMapper.toUserDto(userToSave);
        UserDto actualUserDto = userService.create(userDtoToSave);

        assertEquals(userDtoToSave, actualUserDto);
        verify(userRepository, times(1))
                .save(userToSave);
    }

    @Test
    void create_whenEmailNotValid_thenNotSavedUser() {
        User userToSave = new User();
        UserDto userDtoToSave = UserMapper.toUserDto(userToSave);

        assertThrows(UserWithoutEmailException.class, () -> userService.create(userDtoToSave));

        verify(userRepository, never()).save(userToSave);
    }

    @Test
    void create_whenEmailDuplicate_thenNotSavedUser() {
        String duplicateEmail = "duplicate@email.ru";
        User userToSave = new User();
        userToSave.setEmail(duplicateEmail);
        UserDto userDtoToSave = UserMapper.toUserDto(userToSave);

        when(userRepository.save(userToSave)).thenThrow(DuplicateEmailException.class);

        assertThrows(DuplicateEmailException.class, () -> userService.create(userDtoToSave));

        verify(userRepository, times(1)).save(userToSave);
    }

    @Test
    void update_whenUserFound_thenUpdateAvailableFields() {
        int userId = 1;
        User oldUser = new User();
        oldUser.setId(userId);
        oldUser.setName("oldName");
        oldUser.setEmail("oldemail@ya.ru");

        User newUser = new User();
        newUser.setId(userId);
        newUser.setName("newName");
        newUser.setEmail("newEmail@ya.ru");
        UserDto newUserDto = UserMapper.toUserDto(newUser);

        when(userRepository.save(newUser)).thenReturn(newUser);

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        UserDto actualUserDto = userService.update(userId, newUserDto);
        User actualUser = UserMapper.toUser(actualUserDto);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals("newName", savedUser.getName());
        assertEquals("newEmail@ya.ru", savedUser.getEmail());
        assertEquals(savedUser, actualUser);
    }

    @Test
    void get_whenUserFound_thenReturnedUser() {
        int userId = 0;
        User expectedUser = new User();
        UserDto expectedUserDto = UserMapper.toUserDto(expectedUser);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserDto actualUserDto = userService.get(userId);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void get_whenUserNotFound_thenUserNotFoundExceptionThrow() {
        int userId = 0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        var userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> userService.get(userId));

        String expectedMessage = "User with ID:" + userId + " not found";

        assertEquals(expectedMessage, userNotFoundException.getMessage());
    }

    @Test
    void getAll() {
        List<User> expectedUsers = List.of(new User(), new User());

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> actualUsers = userService.getAll();

        verify(userRepository, times(1)).findAll();
        assertEquals(UserMapper.listToUserDto(expectedUsers), actualUsers);
    }

    @Test
    void delete_whenUserFound_thenReturnedTrue() {
        int userId = 1;

        when(userRepository.existsById(userId)).thenReturn(true);

        boolean actualAnswer = userService.delete(userId);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
        assertTrue(actualAnswer);
    }

    @Test
    void delete_whenUserNotFound_thenReturnedFalse() {
        int userId = 1;

        when(userRepository.existsById(userId)).thenReturn(false);

        boolean actualAnswer = userService.delete(userId);

        verify(userRepository, times(1)).existsById(userId);
        assertFalse(actualAnswer);
    }
}