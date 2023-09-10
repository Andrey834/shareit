package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {
    User saveUser(User user);

    User updateUser(User user);

    User getUser(int userId);

    List<User> getUsers();

    void deleteUser(int userId);

}
