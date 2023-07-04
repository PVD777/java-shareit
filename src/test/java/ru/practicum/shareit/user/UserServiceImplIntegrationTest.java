package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplIntegrationTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void getUnknownUser() {
        Mockito
                .when(repository.findById(Mockito.anyInt()))
                .thenThrow(new ObjectNotFoundException("User не найден"));

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> userService.getUser(999));

        assertEquals("User не найден", exception.getMessage());
    }

    @Test
    public void updateUnknownUser() {
        Mockito
                .when(repository.findById(Mockito.anyInt()))
                .thenThrow(new ObjectNotFoundException("User не найден"));

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> userService.updateUser(99, Mockito.any(UserDto.class)));

        Assertions.assertEquals("User не найден", exception.getMessage());

    }

}