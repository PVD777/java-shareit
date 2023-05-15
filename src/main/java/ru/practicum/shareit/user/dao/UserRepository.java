package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    User createUser(User user);

    Optional<User> getUser(int userId);

    Collection<User> getAllUsers();

    User updateUser(int id, User user);

    void deleteUser(int id);

    boolean isExist(String name, String email);

}
