package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {

    @Test
    void toItemRequestDto() {
        ItemRequest itemRequest = new ItemRequest(1, "desc", new User(), LocalDateTime.now());
        ItemRequestDtoOut itemRequestDtoOut = ItemRequestMapper.toItemRequestDto(itemRequest);
        assertEquals(itemRequestDtoOut.getId(), itemRequest.getId());
    }

    @Test
    void dtoToItemRequest() {
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn("asd");
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestDtoIn);
        assertEquals(itemRequestDtoIn.getDescription(), itemRequest.getDescription());
    }
}