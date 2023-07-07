package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utility.XHeaders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    private final List<UserDto> users = new ArrayList<>();

    private final List<ItemDto> items = new ArrayList<>();

    private final List<CommentDto> comments = new ArrayList<>();


    @BeforeEach
    public void beforeEach() {
        users.clear();
        UserDto user1 = new UserDto("User1", "user1@mail.ru");
        user1.setId(1);
        users.add(user1);
        UserDto user2 = new UserDto("User2", "user2@mail.ru");
        user2.setId(2);
        users.add(user2);

        items.clear();
        ItemDto itemDto1 = new ItemDto("Item 1", "Item 1 description", true);
        itemDto1.setId(1);
        items.add(itemDto1);
        ItemDto itemDto2 = new ItemDto("Item 2", "Item 2 description", true);
        itemDto2.setId(2);
        items.add(itemDto2);

        comments.clear();
        CommentDto comment = new CommentDto();
        comment.setId(1);
        comment.setText("Comment1");
        comment.setAuthorName(user1.getName());
        comment.setCreated(LocalDateTime.now());
        comments.add(comment);
    }

    @Test
    void create() throws Exception {
        UserDto user = users.get(0);
        ItemDto item = items.get(0);

        Mockito
                .when(itemService.createItem(Mockito.anyInt(), Mockito.any(ItemDto.class)))
                .thenReturn(item);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/items")
                .header(XHeaders.USER_ID_X_HEADER, user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item))
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable()), Boolean.class));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto item = items.get(0);
        UserDto user = users.get(0);

        Mockito
                .when(itemService.updateItem(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(ItemDto.class)))
                .thenReturn(item);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/items/{itemId}", item.getId())
                                .header(XHeaders.USER_ID_X_HEADER, user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable()), Boolean.class));
    }

    @Test
    void getItemById() throws Exception {
        ItemDto item = items.get(0);
        UserDto user = users.get(0);
        Mockito
                .when(itemService.getItem(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(item);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/items/{id}", item.getId())
                                .header(XHeaders.USER_ID_X_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable()), Boolean.class));
    }

    @Test
    void getByOwner() throws Exception {
        ItemDto item = items.get(0);
        UserDto user = users.get(0);

        Mockito.when(itemService.getOwnersItem(Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/items")
                                .header(XHeaders.USER_ID_X_HEADER, user.getId()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    public void searchItem() throws Exception {
        ItemDto item = items.get(0);
        UserDto user = users.get(0);

        Mockito.when(itemService.getAvailableItems(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(item));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/items/search")
                                .header(XHeaders.USER_ID_X_HEADER, user.getId())
                                .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    public void createComment() throws Exception {

        Mockito
                .when(itemService.addComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(CommentDto.class)))
                .thenThrow(new ObjectNotFoundException(("Item не найден")));
        ItemDto item = items.get(0);
        UserDto user = users.get(0);
        CommentDto comment = comments.get(0);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/items/{itemId}/comment", item.getId())
                                .header(XHeaders.USER_ID_X_HEADER, user.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error",
                        containsStringIgnoringCase("Item не найден")));
    }
}