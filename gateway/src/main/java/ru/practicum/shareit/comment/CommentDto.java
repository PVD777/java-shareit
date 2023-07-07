package ru.practicum.shareit.comment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    int id;
    @NotEmpty
    String text;
    String authorName;
    LocalDateTime created;
}
