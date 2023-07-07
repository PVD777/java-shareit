package ru.practicum.shareit.comment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
//@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    int id;
    //@NotEmpty
    String text;
    String authorName;
    LocalDateTime created;
}
