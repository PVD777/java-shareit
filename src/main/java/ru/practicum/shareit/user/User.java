package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * TODO Sprint add-controllers.
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class User {
    int id;
    @NotEmpty
    String name;
    @Email
    @NotEmpty
    String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
