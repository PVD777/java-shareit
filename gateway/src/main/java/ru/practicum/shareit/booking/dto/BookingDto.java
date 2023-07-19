package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.utility.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@StartBeforeEndDateValid
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    @NotNull
    int itemId;
    @FutureOrPresent
    LocalDateTime start;
    LocalDateTime end;
}
