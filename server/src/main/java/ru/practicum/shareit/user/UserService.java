package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getUser(int userId);

    Collection<UserDto> getAllUsers();

    UserDto updateUser(int id, UserDto userDto);

    void deleteUser(int id);
}
