package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utility.ExistValidator;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUser(int userId) {
        return UserMapper.toUserDto(ExistValidator.validateUser(userRepository, userId));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(int id, UserDto userDto) {
        User oldUser = UserMapper.dtoToUser(getUser(id));
        User patchedUser = UserMapper.dtoToUser(userDto);
        if (patchedUser.getEmail() != null
                && !patchedUser.getEmail().matches("^[a-zA-Z0-9_!#$%&'*+/=?``{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            throw new ValidationException("Неверный email");
        }
        patchedUser.setId(id);
        if (patchedUser.getName() == null) {
            patchedUser.setName(oldUser.getName());
        }
        if (patchedUser.getEmail() == null) {
            patchedUser.setEmail(oldUser.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(patchedUser));
    }


    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
