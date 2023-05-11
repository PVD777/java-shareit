package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    User createUser(User user);
    User getUser(int userId);
    Collection<User> getAllUsers();
    User updateUser(User user);
    void deleteUser(int id);
}
