package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoOutShort;
import ru.practicum.shareit.booking.model.Booking;

@UtilityClass
public class BookingMapper {

    BookingDtoOut bookingToDtoOut(Booking booking) {
        BookingDtoOut bookingDtoOut = new BookingDtoOut();
        bookingDtoOut.setId(booking.getId());
        bookingDtoOut.setStart(booking.getBookingStart());
        bookingDtoOut.setEnd(booking.getBookingFinish());
        bookingDtoOut.setStatus(booking.getStatus());
        bookingDtoOut.setBooker(booking.getUser());
        bookingDtoOut.setItem(booking.getItem());
        return bookingDtoOut;
    }

    Booking dtoToBooking(BookingDtoIn bookingDto) {
        return new Booking(bookingDto.getStart(), bookingDto.getEnd());
    }

    public BookingDtoOutShort bookingToDtoOutShort(Booking booking) {
        BookingDtoOutShort bookingDtoOutShort = new BookingDtoOutShort();
        bookingDtoOutShort.setId(booking.getId());
        bookingDtoOutShort.setStart(booking.getBookingStart());
        bookingDtoOutShort.setEnd(booking.getBookingFinish());
        bookingDtoOutShort.setStatus(booking.getStatus());
        bookingDtoOutShort.setBookerId(booking.getUser().getId());
        bookingDtoOutShort.setItemId(booking.getItem().getId());
        return bookingDtoOutShort;
    }

    BookingDtoIn bookingToDtoIn(Booking booking) {
        return new BookingDtoIn(booking.getItem().getId(), booking.getBookingStart(), booking.getBookingFinish());
    }

}
