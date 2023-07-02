package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;


    @Override
    public BookingDtoOut createBooking(int userId, BookingDtoIn bookingDtoIn) {

        Item item = validateItem(bookingDtoIn.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна");
        }
        User user = validateUser(userId);
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
        Booking booking = validateBooking(bookingId);
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
        Booking booking = validateBooking(bookingId);
        if (booking.getUser().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new ObjectNotFoundException("Доступ запрещен");
        }
        return BookingMapper.bookingToDtoOut(booking);
    }

    public Collection<BookingDtoOut> getBookingsOfUser(int userId, String state, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователь не существует");
        }
        if (!BookingSearchStatus.contains(state)) {
            throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        List<Booking> bookingsOfUser = new ArrayList<>();
        switch (BookingSearchStatus.valueOf(state)) {
            case ALL:
                bookingsOfUser = bookingRepository.findBookingsByUserId(userId, pageable);
                break;
            case PAST:
                bookingsOfUser = bookingRepository.findBookingsByUserIdAndBookingFinishBefore(userId,
                        LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookingsOfUser = bookingRepository.findBookingsByUserIdAndBookingStartAfter(userId,
                        LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookingsOfUser = bookingRepository.findBookingsByUserIdAndBookingStartBeforeAndBookingFinishAfter(userId,
                        LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookingsOfUser = bookingRepository.findBookingsByUserIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingsOfUser = bookingRepository.findBookingsByUserIdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        return bookingsOfUser.stream()
                .sorted(Comparator.comparing(Booking::getBookingStart).reversed())
                .map(BookingMapper::bookingToDtoOut)
                .collect(Collectors.toList());
    }


    public Collection<BookingDtoOut> getBookingsOfOwner(int userId, String state, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователь не существует");
        }
        if (!BookingSearchStatus.contains(state)) {
            throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        List<Booking> bookingsOfOwner = new ArrayList<>();
        switch (BookingSearchStatus.valueOf(state)) {
            case ALL:
                bookingsOfOwner = bookingRepository.findBookingsByItemOwnerId(userId, pageable);
                break;
            case PAST:
                bookingsOfOwner = bookingRepository.findBookingsByItemOwnerIdAndBookingFinishBefore(userId,
                        LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookingsOfOwner = bookingRepository.findBookingsByItemOwnerIdAndBookingStartAfter(userId,
                        LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookingsOfOwner = bookingRepository.findBookingsByItemOwnerIdAndBookingStartBeforeAndBookingFinishAfter(userId,
                        LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookingsOfOwner = bookingRepository.findBookingsByItemOwnerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingsOfOwner = bookingRepository.findBookingsByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        return bookingsOfOwner.stream()
                .sorted(Comparator.comparing(Booking::getBookingStart).reversed())
                .map(BookingMapper::bookingToDtoOut)
                .collect(Collectors.toList());
    }

    private Item validateItem(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный item не найден"));
    }

    private User validateUser(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный User не найден"));
    }

    private Booking validateBooking(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрошенный Booking не найден"));
    }
}
