package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    int id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments = new ArrayList<>();
    Integer requestId;
}