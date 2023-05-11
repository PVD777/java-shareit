package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;

public interface UserRepository {
    User createUser(User user);
    User getUser(int userId);
    Collection<User> getAllUsers();
    User updateUser(User user);
    void deleteUser(int id);
}
