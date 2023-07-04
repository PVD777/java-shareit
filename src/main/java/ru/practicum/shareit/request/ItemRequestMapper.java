package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

public class ItemRequestMapper {

    public static ItemRequestDtoOut toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDtoOut itemRequestDtoOut = new ItemRequestDtoOut();
        itemRequestDtoOut.setDescription(itemRequest.getDescription());
        itemRequestDtoOut.setId(itemRequest.getId());
        itemRequestDtoOut.setCreated(itemRequest.getCreatedDateTime());
        return itemRequestDtoOut;
    }

    public static ItemRequest dtoToItemRequest(ItemRequestDtoIn itemRequestDtoIn) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDtoIn.getDescription());
        return itemRequest;
    }

    public static ItemRequestDtoIn toItemRequestDtoIn(ItemRequest itemRequest) {
        return new ItemRequestDtoIn(itemRequest.getDescription());
    }
}
