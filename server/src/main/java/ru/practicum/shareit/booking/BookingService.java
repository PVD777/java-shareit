package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.dto.BookingDtoIn;
import ru.practicum.shareit.booking.model.dto.BookingDtoOut;

import java.util.Collection;

public interface BookingService {

    BookingDtoOut createBooking(int userId, BookingDtoIn bookingDto);

    BookingDtoOut giveApprove(int userId, int bookingId, boolean isApproved);

    BookingDtoOut getBooking(int bookingId, int userId);

    Collection<BookingDtoOut> getBookingsOfUser(int userId, String state, Pageable pageable);

    Collection<BookingDtoOut> getBookingsOfOwner(int userId, String state, Pageable pageable);
}
