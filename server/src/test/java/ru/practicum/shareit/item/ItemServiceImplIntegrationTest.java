package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ItemServiceImplIntegrationTest {
    @Autowired
    ItemService itemService;
    @Autowired
    private UserService userService;

    private final List<UserDto> users = new ArrayList<>();
    private final List<ItemDto> items = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        users.clear();
        UserDto user1 = new UserDto("User1", "user1@mail.ru");
        user1.setId(1);
        users.add(user1);
        UserDto user2 = new UserDto("User2", "user2@mail.ru");
        user2.setId(2);
        users.add(user2);

        ItemDto itemDto1 = new ItemDto("Item 1", "Item 1 description", true);
        itemDto1.setId(1);
        items.add(itemDto1);
        ItemDto itemDto2 = new ItemDto("Item 2", "Item 2 description", true);
        itemDto2.setId(2);
        items.add(itemDto2);
    }

    @Test
    void createWithNoExistUser() {
        Exception exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> itemService.createItem(999, items.get(0)));
        assertEquals("Запрошенный User не найден", exception.getMessage());
    }

    @Test
    public void updateWithNoAccess() {
        UserDto user = users.get(0);
        userService.createUser(user);

        ItemDto expectedItem = new ItemDto();
        expectedItem.setId(items.get(0).getId());
        expectedItem.setName(items.get(0).getName());
        expectedItem.setDescription("Item 1 updated description");
        expectedItem.setAvailable(items.get(0).getAvailable());

        itemService.createItem(user.getId(), items.get(0));
        assertThrows(ForbiddenException.class,() -> itemService.updateItem(3,expectedItem.getId(),expectedItem));
    }
}