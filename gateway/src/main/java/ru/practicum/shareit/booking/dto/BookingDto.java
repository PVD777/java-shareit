package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.utility.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@StartBeforeEndDateValid
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    @FutureOrPresent
    LocalDateTime start;
    @Future
    LocalDateTime end;
}
