package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.dto.BookingDtoOutShort;
import ru.practicum.shareit.comment.CommentDto;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    int id;
    String name;
    String description;
    Boolean available;
    BookingDtoOutShort lastBooking;
    BookingDtoOutShort nextBooking;
    List<CommentDto> comments = new ArrayList<>();
    Integer requestId;

    public ItemDto(int id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
