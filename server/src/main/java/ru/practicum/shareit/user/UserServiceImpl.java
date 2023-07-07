package ru.practicum.shareit.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;

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
        return UserMapper.toUserDto(validateUser(userId));
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
        patchedUser.setId(id);
        if (patchedUser.getName() == null || patchedUser.getName().isBlank()) {
            patchedUser.setName(oldUser.getName());
        }
        if (patchedUser.getEmail() == null || patchedUser.getEmail().isBlank()) {
            patchedUser.setEmail(oldUser.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(patchedUser));
    }


    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    private User validateUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный User не найден"));
    }
}
