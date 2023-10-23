package ru.practicum.server.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserDto {
    private long id;
    private String name;
    @Email
    private String email;
}
