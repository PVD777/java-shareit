package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.utility.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@StartBeforeEndDateValid
public class BookingDto {

    int id;
    @NotNull
    @Future
    LocalDateTime start;
    @NotNull
    @Future
    LocalDateTime end;
    BookingStatus status;
    int bookerId;
    int itemId;
}
