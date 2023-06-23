package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoOutShort;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static BookingDtoOut bookingToDtoOut(Booking booking) {
        BookingDtoOut bookingDtoOut = new BookingDtoOut();
        bookingDtoOut.setId(booking.getId());
        bookingDtoOut.setStart(booking.getBookingStart());
        bookingDtoOut.setEnd(booking.getBookingFinish());
        bookingDtoOut.setStatus(booking.getStatus());
        bookingDtoOut.setBooker(booking.getUser());
        bookingDtoOut.setItem(booking.getItem());
        return bookingDtoOut;
    }

    public static Booking dtoToBooking(BookingDtoIn bookingDto) {
        return new Booking(bookingDto.getStart(),bookingDto.getEnd());
    }

    public static BookingDtoOutShort bookingToDtoOutShort(Booking booking) {
        BookingDtoOutShort bookingDtoOutShort = new BookingDtoOutShort();
        bookingDtoOutShort.setId(booking.getId());
        bookingDtoOutShort.setStart(booking.getBookingStart());
        bookingDtoOutShort.setEnd(booking.getBookingFinish());
        bookingDtoOutShort.setStatus(booking.getStatus());
        bookingDtoOutShort.setBookerId(booking.getUser().getId());
        bookingDtoOutShort.setItemId(booking.getItem().getId());
        return bookingDtoOutShort;
    }

    public static BookingDtoIn bookingToDtoIn(Booking booking) {
        return new BookingDtoIn(booking.getItem().getId(), booking.getBookingStart(), booking.getBookingFinish());
    }

}
