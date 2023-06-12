package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingDtoOut {
    int id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status = BookingStatus.WAITING;
    User booker;
    Item item;
}
