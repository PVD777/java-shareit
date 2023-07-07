package ru.practicum.shareit.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor

public class BookingDtoIn {
    int itemId;
    LocalDateTime start;
    LocalDateTime end;
}
