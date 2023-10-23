package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserWithoutEmailException;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto, HttpServletRequest request) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new UserWithoutEmailException("without email");
        }
        log.info("***Creating a user with EMAIL: {} from an IP: {}", userDto.getEmail(), request.getLocalAddr());
        return userClient.create(userDto);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> update(
            @RequestBody @Valid UserDto userDto,
            @PathVariable long id,
            HttpServletRequest request
    ) {
        log.info("***Updating user ID: {} from an IP: {}", id, request.getLocalAddr());
        return userClient.update(id, userDto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> get(@PathVariable long id, HttpServletRequest request) {
        log.info("***User request with ID: {} from IP: {}", id, request.getLocalAddr());
        return userClient.get(id);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers(HttpServletRequest request) {
        log.info("***Query all users from an IP: {}", request.getLocalAddr());
        return userClient.getAll();
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void delete(@PathVariable long id, HttpServletRequest request) {
        log.info("***Delete user with ID: {} from an IP: {}", id, request.getLocalAddr());
        userClient.delete(id);
    }
}
