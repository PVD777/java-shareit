package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto create(int userId, ItemRequestDto itemRequestDto);

    ItemRequestDto get(int requestId, int userId);

    Collection<ItemRequestDto> getOwnerRequests(int userId);

    Collection<ItemRequestDto> getAll(int userId, int from, int size);
}