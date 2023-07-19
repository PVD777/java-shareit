package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    void toUserDto() {
        User user = new User("user1", "user1@mail.com");
        user.setId(1);
        UserDto userDto = UserMapper.toUserDto(user);
        assertEquals(user.getId(),userDto.getId());
        assertEquals(user.getName(),userDto.getName());
        assertEquals(user.getEmail(),userDto.getEmail());
    }

    @Test
    void dtoToUser() {
        UserDto userDto = new UserDto(1, "name", "mail@mail.com");
        User user = UserMapper.dtoToUser(userDto);
        assertEquals(user.getName(),userDto.getName());
        assertEquals(user.getEmail(),userDto.getEmail());
    }
}