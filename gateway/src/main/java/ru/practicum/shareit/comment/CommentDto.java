package ru.practicum.shareit.comment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    @NotEmpty
    String text;
}
