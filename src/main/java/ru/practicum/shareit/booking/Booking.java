package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {

    int id;
    LocalDateTime bookingStart;
    LocalDateTime bookingFinish;
    Item item;
    BookingStatus bookingStatus;
    User user;
}
