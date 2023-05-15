package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectAlreadyExistException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    @Qualifier(value = "InMemoryUserRepository")
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        if (userRepository.isExist(user.getName(), user.getEmail()))
            throw new ObjectAlreadyExistException("Пользователь с данными пораметрами уже существует");
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto getUser(int userId) {
        return UserMapper.toUserDto(userRepository.getUser(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден")));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(user -> UserMapper.toUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(int id, UserDto userDto) {
        User oldUser = UserMapper.dtoToUser(getUser(id));
        User patchedUser = UserMapper.dtoToUser(userDto);
        if (userRepository.isExist(patchedUser.getName(), patchedUser.getEmail()) &&
                (patchedUser.getName() != null && !patchedUser.getName().equals(oldUser.getName()) ||
                        patchedUser.getEmail() != null && !patchedUser.getEmail().equals(oldUser.getEmail())))
            throw new ObjectAlreadyExistException("Пользователь с данными пораметрами уже существует");

        patchedUser.setId(id);
        if (patchedUser.getName() == null) {
            patchedUser.setName(oldUser.getName());
        }
        if (patchedUser.getEmail() == null) {
            patchedUser.setEmail(oldUser.getEmail());
        }
        return UserMapper.toUserDto(userRepository.updateUser(id, patchedUser));
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteUser(id);
    }
}
