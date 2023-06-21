package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto() {
        User user = new User("user1", "user1@mail.com");
        user.setId(1);
        ItemRequest itemRequest = new ItemRequest(1, "reqDesc1", user, LocalDateTime.now().minusDays(1));
        Item item = new Item(1, "itemName1", "itemDesc1", true);
        item.setOwner(user);
        item.setRequest(itemRequest);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertEquals(itemDto.getId(),item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(),item.getDescription());
        assertEquals(itemDto.getRequestId(),item.getRequest().getId());
    }

    @Test
    void dtoToItem() {
        ItemDto itemDto = new ItemDto(1, "dtoName", "dtoDesc", true);
        Item item = ItemMapper.dtoToItem(itemDto);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }
}