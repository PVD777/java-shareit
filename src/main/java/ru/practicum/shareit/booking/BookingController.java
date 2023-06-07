package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;


    @PostMapping
    public BookingDtoOut createBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                       @Valid @RequestBody BookingDtoIn bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoOut giveApprove(@RequestHeader("X-Sharer-User-Id") int userId,
                                    @RequestParam boolean approved, @PathVariable int bookingId) {
        return bookingService.giveApprove(userId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingDtoOut getBooking(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDtoOut> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") int userId,
                                                       @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsOfUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoOut> getBookedItemOfOwner(@RequestHeader("X-Sharer-User-Id") int userId,
                                                          @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsOfOwner(userId, state);
    }
}


