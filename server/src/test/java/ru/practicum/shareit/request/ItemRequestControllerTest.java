package ru.practicum.shareit.request;


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
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utility.XHeaders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    private final List<UserDto> users = new ArrayList<>();

    private final List<ItemRequestDtoOut> requests = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        users.clear();
        UserDto user1 = new UserDto();
        user1.setId(1);
        user1.setName("User 1");
        user1.setEmail("user1@yandex.ru");
        users.add(user1);
        UserDto user2 = new UserDto();
        user2.setId(2);
        user2.setName("User 2");
        user2.setEmail("user2@yandex.ru");
        users.add(user2);

        ItemRequestDtoOut itemRequest1 = new ItemRequestDtoOut();
        itemRequest1.setId(1);
        itemRequest1.setDescription("desc1");
        itemRequest1.setCreated(LocalDateTime.now());
        ItemRequestDtoOut itemRequest2 = new ItemRequestDtoOut();
        itemRequest2.setId(2);
        itemRequest2.setDescription("desc2");
        itemRequest2.setCreated(LocalDateTime.now());
        requests.add(itemRequest1);
        requests.add(itemRequest2);

    }

    @Test
    void createRequest() throws Exception {
        ItemRequestDtoOut request = requests.get(0);
        UserDto user = users.get(0);
        Mockito.when(itemRequestService.create(Mockito.anyInt(), Mockito.any(ItemRequestDtoIn.class)))
                .thenReturn(request);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/requests")
                                .header(XHeaders.USER_ID_X_HEADER, user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())));
    }

    @Test
    void getRequest() {
    }

    @Test
    void getOwnerRequests() {
    }

    @Test
    void getAllRequests() throws Exception {
        ItemRequestDtoOut request = requests.get(0);
        UserDto user = users.get(0);
        Mockito
                .when(itemRequestService.getOwnerRequests(Mockito.anyInt()))
                .thenReturn(List.of(request));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/requests")
                                .header(XHeaders.USER_ID_X_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }
}