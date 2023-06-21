package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;


@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    int id;
    @NotEmpty(message = "Пустое имя")
    String name;
    @Email(message = "Неверный формат email")
    @NotEmpty(message = "Email не заполнен")
    String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
