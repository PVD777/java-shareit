package ru.practicum.shareit.booking.model.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
//@NoArgsConstructor
public class BookingDtoOutShort {

    int id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    int bookerId;
    int itemId;
}
