package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDtoOut create(int userId, ItemRequestDtoIn itemRequestDtoIn);

    ItemRequestDtoOut get(int requestId, int userId);

    Collection<ItemRequestDtoOut> getOwnerRequests(int userId);

    Collection<ItemRequestDtoOut> getAll(int userId, int from, int size);
}