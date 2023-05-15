package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    int id;
    @NotEmpty(message = "Пустое имя")
    String name;
    @Email(message = "Неверный формат email")
    @NotEmpty(message = "Email не заполнен")
    String email;
}
