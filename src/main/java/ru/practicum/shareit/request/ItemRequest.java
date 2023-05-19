package ru.practicum.shareit.request;
import ru.practicum.shareit.user.User;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    int id;
    String description;
    User user;
    LocalDateTime createdDateTime;

}
