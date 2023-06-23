package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utility.XHeaders;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    private final List<User> users = new ArrayList<>();

    private final List<Item> items = new ArrayList<>();

    private final List<Booking> bookings = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        users.clear();
        User user1 = new User();
        user1.setId(1);
        user1.setName("User 1");
        user1.setEmail("user1@yandex.ru");
        users.add(user1);
        User user2 = new User();
        user2.setId(2);
        user2.setName("User 2");
        user2.setEmail("user2@yandex.ru");
        users.add(user2);

        items.clear();
        Item item1 = new Item();
        item1.setId(1);
        item1.setName("Item 1");
        item1.setDescription("Item description 1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        items.add(item1);
        Item item2 = new Item();
        item2.setId(2);
        item2.setName("Item 1");
        item2.setDescription("Item description 1");
        item2.setAvailable(true);
        item2.setOwner(user1);
        items.add(item2);

        bookings.clear();
        Booking booking1 = new Booking();
        booking1.setId(1);
        booking1.setItem(item1);
        booking1.setUser(user2);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setBookingStart(LocalDateTime.now().plusDays(1));
        booking1.setBookingFinish(LocalDateTime.now().plusDays(2));
        bookings.add(booking1);
        Booking booking2 = new Booking();
        booking2.setId(2);
        booking2.setItem(item1);
        booking2.setUser(user2);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setBookingStart(LocalDateTime.now().plusDays(3));
        booking2.setBookingFinish(LocalDateTime.now().plusDays(4));
        bookings.add(booking2);
        Booking booking3 = new Booking();
        booking3.setId(3);
        booking3.setItem(item1);
        booking3.setUser(user2);
        booking3.setStatus(BookingStatus.APPROVED);
        booking3.setBookingStart(LocalDateTime.now().minusHours(3));
        booking3.setBookingFinish(LocalDateTime.now().plusHours(4));
        bookings.add(booking3);


    }

    @Test
    void createBooking() throws Exception {
        User user = users.get(0);
        BookingDtoIn booking = BookingMapper.bookingToDtoIn(bookings.get(0));

        Mockito
                .when(bookingService.createBooking(Mockito.anyInt(), Mockito.any(BookingDtoIn.class)))
                .thenReturn(BookingMapper.bookingToDtoOut(bookings.get(0)));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking))
                .header(XHeaders.USER_ID_X_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookings.get(0).getId()), Integer.class));
    }

    @Test
    public void createBookingFailed() throws Exception {
        User user = users.get(0);
        BookingDtoIn booking = BookingMapper.bookingToDtoIn(bookings.get(0));

        Mockito
                .when(bookingService.createBooking(Mockito.anyInt(), Mockito.any(BookingDtoIn.class)))
                .thenThrow(new ValidationException("Some valid exp"));

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking))
                        .header(XHeaders.USER_ID_X_HEADER, user.getId()))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error",
                        containsStringIgnoringCase("Some valid exp")));
    }

    @Test
    void giveApprove() throws Exception {
        User user = users.get(0);
        BookingDtoIn bookingIn = BookingMapper.bookingToDtoIn(bookings.get(0));

        Mockito
                .when(bookingService.giveApprove(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean()))
                .thenReturn(BookingMapper.bookingToDtoOut(bookings.get(0)));

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/bookings/{bookingId}", bookings.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingIn))
                        .header(XHeaders.USER_ID_X_HEADER, user.getId())
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookings.get(0).getId()), Integer.class));
    }

    @Test
    void getBooking() throws Exception {

        User user = users.get(0);
        BookingDtoIn booking = BookingMapper.bookingToDtoIn(bookings.get(0));

        Mockito
                .when(bookingService.getBooking(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(BookingMapper.bookingToDtoOut(bookings.get(0)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bookings/{bookingId}", bookings.get(0).getId())
                        .header(XHeaders.USER_ID_X_HEADER, user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookings.get(0).getId()), Integer.class));
    }

}