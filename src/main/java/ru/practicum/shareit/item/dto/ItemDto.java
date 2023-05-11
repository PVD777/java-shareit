package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    int id;
    String name;
    String description;
    boolean available;
    User owner;
    ItemRequest request;
}
