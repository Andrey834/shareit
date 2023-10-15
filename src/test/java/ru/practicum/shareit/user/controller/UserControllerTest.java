package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Test
    void create_whenInvoked_thenResponseStatusOkWithUserInBody() {
        UserDto expectedUserDto = new UserDto();
        when(userService.create(new UserDto())).thenReturn(expectedUserDto);

        ResponseEntity<UserDto> response = userController.create(expectedUserDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserDto, response.getBody());
    }

    @Test
    void update_whenInvoked_thenResponseStatusOkWithUserInBody() {
        int userId = 0;
        UserDto expectedUserDto = new UserDto();
        when(userService.update(userId, new UserDto())).thenReturn(expectedUserDto);

        ResponseEntity<UserDto> response = userController.update(userId, expectedUserDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserDto, response.getBody());
    }

    @Test
    void get() {
        int userId = 0;
        UserDto expectedUserDto = new UserDto();
        when(userService.get(userId)).thenReturn(expectedUserDto);

        ResponseEntity<UserDto> response = userController.get(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserDto, response.getBody());
    }

    @Test
    void getAll_whenInvoked_thenResponseStatusOkWithUsersCollectionInBody() {
        List<UserDto> expectedUsersDto = List.of(new UserDto());
        when(userService.getAll()).thenReturn(expectedUsersDto);

        ResponseEntity<List<UserDto>> response = userController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsersDto, response.getBody());
    }

    @Test
    void delete_whenInvoked_thenResponseStatusOkWithBooleanTrue() {
        int userId = 0;
        boolean expectedBoolean = true;
        when(userService.delete(userId)).thenReturn(expectedBoolean);

        ResponseEntity<Boolean> response = userController.delete(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedBoolean, response.getBody());
    }
}