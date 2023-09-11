package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {
    User save(User user);

    User update(User user);

    User get(int userId);

    List<User> getAll();

    void delete(int userId);

}
