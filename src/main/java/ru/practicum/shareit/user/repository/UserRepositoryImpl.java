package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        final int userId = user.getId();
        users.put(userId, user);
        return users.get(userId);
    }

    @Override
    public User update(User user) {
        final int userId = user.getId();
        users.put(userId, user);
        return users.get(userId);
    }

    @Override
    public User get(int userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(int userId) {
        users.remove(userId);
    }
}
