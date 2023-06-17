package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.utility.XHeaders;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;


@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;


    @PostMapping
    public BookingDtoOut createBooking(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId,
                                       @Valid @RequestBody BookingDtoIn bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoOut giveApprove(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId,
                                    @RequestParam boolean approved, @PathVariable int bookingId) {
        return bookingService.giveApprove(userId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingDtoOut getBooking(@PathVariable int bookingId, @RequestHeader(XHeaders.USER_ID_X_HEADER) int userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDtoOut> getBookingsOfUser(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "999") @Positive int size) {
        return bookingService.getBookingsOfUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoOut> getBookedItemOfOwner(@RequestHeader(XHeaders.USER_ID_X_HEADER) int userId,
                                                          @RequestParam(defaultValue = "ALL") String state,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                          @RequestParam(defaultValue = "999") @Positive int size) {
        return bookingService.getBookingsOfOwner(userId, state, from, size);
    }
}


