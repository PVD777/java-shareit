package ru.practicum.shareit.utility;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

@Generated
@UtilityClass
public class ExistValidator {

    public static Item validateItem(ItemRepository itemRepository, int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный item не найден"));
    }

    public static User validateUser(UserRepository userRepository, int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный User не найден"));
    }

    public static Booking validateBooking(BookingRepository bookingRepository, int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный Booking не найден"));
    }

    public static ItemRequest validateRequest(RequestRepository itemRequestRepository, int requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный Request не найден"));
    }
}
