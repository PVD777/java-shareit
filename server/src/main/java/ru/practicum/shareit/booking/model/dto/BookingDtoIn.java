package ru.practicum.shareit.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor

public class BookingDtoIn {
    //@NotNull
    int itemId;
    //@NotNull
    //@FutureOrPresent
    LocalDateTime start;
    //@NotNull
    //@Future
    LocalDateTime end;
}
