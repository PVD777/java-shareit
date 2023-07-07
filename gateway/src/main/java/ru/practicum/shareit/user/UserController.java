package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.utility.Create;
import ru.practicum.shareit.utility.Update;


@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(value = Create.class) UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> update(@PathVariable int userId, @Validated(value = Update.class) UserDto userDto) {
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> getUser(@PathVariable int userId) {
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }


    @DeleteMapping("{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable int userId) {
        return userClient.deleteUser(userId);
    }
}