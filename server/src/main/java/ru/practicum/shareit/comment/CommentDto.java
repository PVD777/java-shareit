package ru.practicum.shareit.comment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CommentDto {
    int id;
    String text;
    String authorName;
    LocalDateTime created;
}
