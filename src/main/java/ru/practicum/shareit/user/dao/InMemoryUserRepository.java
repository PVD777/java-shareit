package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private int userIdCounter = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        user.setId(++userIdCounter);
        users.put(userIdCounter,user);
        return user;
    }

    @Override
    public User getUser(int userId) {
        return users.get(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int id) {
        users.remove(id);
    }
}
