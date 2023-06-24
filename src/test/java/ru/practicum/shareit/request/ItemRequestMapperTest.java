package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {

    @Test
    void toItemRequestDto() {
        ItemRequest itemRequest = new ItemRequest(1, "desc", new User(), LocalDateTime.now());
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        assertEquals(itemRequestDto.getId(), itemRequest.getId());
    }

    @Test
    void dtoToItemRequest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "asd", LocalDateTime.now(), new ArrayList<>());
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestDto);
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
    }
}