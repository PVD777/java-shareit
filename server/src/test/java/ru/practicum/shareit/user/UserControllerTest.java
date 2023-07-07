package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    List<UserDto> users = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        users.clear();
        UserDto user1 = new UserDto(1, "user1", "user1@yandex.ru");
        users.add(user1);
        UserDto user2 = new UserDto(2, "user2", "user2@yandex.ru");
        users.add(user2);
    }

    @Test
    void createUser() throws Exception {
        UserDto userDto = users.get(0);
        Mockito
                .when(userService.createUser(Mockito.any(UserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getUser() throws Exception {
        UserDto user = users.get(0);
        Mockito
                .when(userService.getUser(Mockito.anyInt()))
                .thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/{id}", user.getId())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void getAllUsers() throws Exception {
        Mockito
                .when(userService.getAllUsers())
                .thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    void updateUser()  throws Exception {
        UserDto user = users.get(0);
        user.setName("updateName");
        user.setEmail("updateemail@mail.asd");
        Mockito
                .when(userService.updateUser(Mockito.anyInt(), Mockito.any(UserDto.class)))
                .thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/{userId}", users.get(0).getId()))
                .andExpect(status().isOk());
    }
}