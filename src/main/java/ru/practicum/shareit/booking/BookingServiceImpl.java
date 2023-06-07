package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingSearchStatus;
import ru.practicum.shareit.booking.model.BookingStatus;

import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


    @Override
    public BookingDtoOut createBooking(int userId, BookingDtoIn bookingDtoIn) {
        Item item = itemRepository.findById(bookingDtoIn.getItemId())
                .orElseThrow(() -> new ObjectNotFoundException("Попытка забронирать несуществующую вещь"));
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Попытка забронировать несуществующим пользователем"));
        if (user.getId() == item.getOwner().getId()) {
            throw new ObjectNotFoundException("Нельзя бронировать свои вещи");
        }
        if (bookingDtoIn.getStart().isAfter(bookingDtoIn.getEnd()) ||
                bookingDtoIn.getStart().isBefore(LocalDateTime.now()) ||
                bookingDtoIn.getStart().equals(bookingDtoIn.getEnd())) {
            throw new ValidationException("Проверь срок аренды");
        }

        Booking booking = BookingMapper.dtoToBooking(bookingDtoIn);
        booking.setItem(item);
        booking.setUser(user);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.bookingToDtoOut(bookingRepository.save(booking));
    }


    public BookingDtoOut giveApprove(int userId, int bookingId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Указнное бронирование не существует"));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new ObjectNotFoundException("Подтверждать может только владелец");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Запрос не в стадии запроса подтверждения");
        }
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.bookingToDtoOut(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOut getBooking(int bookingId, int userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Указнное бронирование не существует"));
        if (booking.getUser().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new ObjectNotFoundException("Доступ запрещен");
        }
        return BookingMapper.bookingToDtoOut(booking);
    }

    public Collection<BookingDtoOut> getBookingsOfUser(int userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не существует"));
        List<Booking> bookings = bookingRepository.getBookingsByUserId(userId);
        return getBookingsOfCondition(bookings, state);
    }


    public Collection<BookingDtoOut> getBookingsOfOwner(int userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не существует"));
        List<Booking> bookings = bookingRepository.findBookingsByItemOwnerId(userId);
        return getBookingsOfCondition(bookings, state);
    }

    private Collection<BookingDtoOut> getBookingsOfCondition(List<Booking> bookings, String state) {
        if (!BookingSearchStatus.contains(state)) {
            throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }

        switch (BookingSearchStatus.valueOf(state)) {
            case ALL:
                bookings = bookings.stream()
                        .sorted(Comparator.comparing(Booking::getBookingStart).reversed())
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookings = bookings.stream()
                        .filter(b -> b.getBookingFinish().isBefore(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getBookingStart).reversed())
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = bookings.stream()
                        .filter(b -> b.getBookingStart().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getBookingStart).reversed())
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookings.stream()
                        .filter(b -> b.getBookingStart().isBefore(LocalDateTime.now()) &&
                                b.getBookingFinish().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getBookingStart).reversed())
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookings.stream()
                        .filter(b -> b.getStatus().equals(BookingStatus.WAITING))
                        .sorted(Comparator.comparing(Booking::getBookingStart).reversed())
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = bookings.stream()
                        .filter(b -> b.getStatus().equals(BookingStatus.REJECTED))
                        .sorted(Comparator.comparing(Booking::getBookingStart).reversed())
                        .collect(Collectors.toList());
                break;
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDtoOut)
                .collect(Collectors.toList());
        }
}
