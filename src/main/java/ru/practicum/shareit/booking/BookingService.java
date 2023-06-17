package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.Collection;

public interface BookingService {

    BookingDtoOut createBooking(int userId, BookingDtoIn bookingDto);

    BookingDtoOut giveApprove(int userId, int bookingId, boolean isApproved);

    BookingDtoOut getBooking(int bookingId, int userId);

    Collection<BookingDtoOut> getBookingsOfUser(int userId, String state, int from, int size);

    Collection<BookingDtoOut> getBookingsOfOwner(int userId, String state, int from, int size);
}
