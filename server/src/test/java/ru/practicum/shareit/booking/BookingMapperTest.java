package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDtoOutShort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    @Test
    void bookingToDtoOutShort() {
        User user = new User("user1", "user1@mail.com");
        user.setId(1);
        Item item = new Item(1, "itemName1", "itemDesc1", true);
        item.setOwner(user);
        Booking booking = new Booking(1, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), item, BookingStatus.APPROVED, user);
        BookingDtoOutShort bookingDtoOutShort = BookingMapper.bookingToDtoOutShort(booking);
        assertEquals(bookingDtoOutShort.getId(), booking.getId());
        assertEquals(bookingDtoOutShort.getItemId(), booking.getItem().getId());
        assertEquals(bookingDtoOutShort.getBookerId(), booking.getUser().getId());
    }
}