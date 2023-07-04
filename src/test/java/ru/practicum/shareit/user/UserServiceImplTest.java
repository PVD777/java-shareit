package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    List<User> users = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        users.clear();
        User user1 = new User("user1", "user1@yandex.ru");
        user1.setId(1);
        users.add(user1);
        User user2 = new User("user2", "user2@yandex.ru");
        user2.setId(2);
        users.add(user2);
    }

    @Test
    void createUser() {
        Mockito
                .when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(users.get(0));

        UserDto userDto = UserMapper.toUserDto(users.get(0));
        UserDto savedUser = userService.createUser(userDto);
        assertNotNull(savedUser);
        assertEquals(users.get(0).getId(), savedUser.getId());
        assertEquals(users.get(0).getName(), savedUser.getName());
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setId(1);
        user.setName("UpdateName");
        user.setEmail("update@update.com");
        UserDto userDto = UserMapper.toUserDto(user);
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(users.get(0)));

        Mockito
                .when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);

        UserDto updatedUser = userService.updateUser(users.get(0).getId(), userDto);
        assertEquals(users.get(0).getId(), updatedUser.getId());
        assertEquals(userDto.getName(), updatedUser.getName());
    }

    @Test
    void updateUserWithNoNameNoMail() {
        User user = new User();
        user.setId(1);
        UserDto userDto = UserMapper.toUserDto(user);
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(users.get(0)));
        Mockito
                .when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(users.get(0));
        UserDto updatedUser = userService.updateUser(users.get(0).getId(), userDto);
        assertEquals(users.get(0).getId(), updatedUser.getId());
        assertEquals(users.get(0).getName(), updatedUser.getName());
    }

    @Test
    void getUser() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(users.get(0)));

        UserDto userDto = userService.getUser(100500);
        assertNotNull(userDto);
        assertEquals(users.get(0).getId(), userDto.getId());
        assertEquals(users.get(0).getName(), userDto.getName());
    }

    @Test
    void getAllUsers() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(users);
        Collection<UserDto> usersDto = userService.getAllUsers();
        assertEquals(users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList()), usersDto);
    }
}