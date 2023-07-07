package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSearchStatus;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Validated
@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody BookingDto bookingDto,
                                         @RequestHeader(name = X_SHARER_USER_ID_HEADER) int userId) {
        return bookingClient.creatBooking(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable int bookingId,
                                          @RequestHeader(name = X_SHARER_USER_ID_HEADER) Long userId,
                                          @RequestParam Boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> get(@PathVariable int bookingId,
                                      @RequestHeader(name = X_SHARER_USER_ID_HEADER) int userId) {
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(name = X_SHARER_USER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "ALL") BookingSearchStatus state,
                                               @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(name = "size", defaultValue = "99") @Positive Integer size) {
        return bookingClient.getByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(name = X_SHARER_USER_ID_HEADER) long userId,
                                                @RequestParam(defaultValue = "ALL") BookingSearchStatus state,
                                                @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(name = "size", defaultValue = "99") @Positive Integer size) {
        return bookingClient.getByOwnerId(userId, state, from, size);
    }
}
