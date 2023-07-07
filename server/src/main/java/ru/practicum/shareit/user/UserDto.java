package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    int id;
    //@NotBlank(groups = {Create.class}, message = "Пустое имя")
    String name;
    //@Email(groups = {Create.class, Update.class}, message = "Неверный формат email")
    //@NotBlank(groups = {Create.class}, message = "Email не заполнен")
    String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
