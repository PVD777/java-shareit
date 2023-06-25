package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    List<User> users = new ArrayList<>();
    List<Item> items = new ArrayList<>();
    List<Booking> bookings = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        users.clear();
        User user1 = new User();
        user1.setId(1);
        user1.setName("User 1");
        user1.setEmail("user1@yandex.ru");
        users.add(user1);
        User user2 = new User();
        user2.setId(2);
        user2.setName("User 2");
        user2.setEmail("user2@yandex.ru");
        users.add(user2);

        items.clear();
        Item item1 = new Item();
        item1.setId(1);
        item1.setName("Item 1");
        item1.setDescription("Item description 1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        items.add(item1);
        Item item2 = new Item();
        item2.setId(2);
        item2.setName("Item 1");
        item2.setDescription("Item description 1");
        item2.setAvailable(true);
        item2.setOwner(user1);
        items.add(item2);

        bookings.clear();
        Booking booking1 = new Booking();
        booking1.setId(1);
        booking1.setItem(item1);
        booking1.setUser(user2);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setBookingStart(LocalDateTime.now().plusDays(1));
        booking1.setBookingFinish(LocalDateTime.now().plusDays(2));
        bookings.add(booking1);
        Booking booking2 = new Booking();
        booking2.setId(2);
        booking2.setItem(item1);
        booking2.setUser(user2);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setBookingStart(LocalDateTime.now().plusDays(3));
        booking2.setBookingFinish(LocalDateTime.now().plusDays(4));
        bookings.add(booking2);
    }


    @Test
    void createBooking() {
        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(users.get(1)));
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(items.get(0)));
        Mockito
                .when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(bookings.get(0));
        BookingDtoIn bookingDtoIn = BookingMapper.bookingToDtoIn(bookings.get(0));
        BookingDtoOut savedBookingDto = bookingService.createBooking(users.get(1).getId(), bookingDtoIn);
        assertEquals(savedBookingDto.getBooker(), bookings.get(0).getUser());
        assertEquals(savedBookingDto.getItem(), bookings.get(0).getItem());
        assertEquals(savedBookingDto.getStart(), bookings.get(0).getBookingStart());
        assertEquals(savedBookingDto.getEnd(), bookings.get(0).getBookingFinish());
    }

    @Test
    void createBookingFail() {
        items.get(0).setAvailable(false);
        Mockito
                .when(itemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(items.get(0)));
        BookingDtoIn bookingDtoIn = BookingMapper.bookingToDtoIn(bookings.get(0));
        ValidationException exception1 = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(users.get(0).getId(), bookingDtoIn));
        assertEquals("Вещь недоступна", exception1.getMessage());


        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(users.get(0)));
        items.get(0).setAvailable(true);
        ObjectNotFoundException exception2 = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.createBooking(users.get(0).getId(), bookingDtoIn));
        assertEquals("Нельзя бронировать свои вещи", exception2.getMessage());


        Mockito
                .when(userRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(users.get(1)));
        bookingDtoIn.setStart(bookingDtoIn.getEnd());
        ValidationException exception3 = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(users.get(1).getId(), bookingDtoIn));
        assertEquals("Проверь срок аренды", exception3.getMessage());

        bookingDtoIn.setStart(bookingDtoIn.getEnd().plusHours(1));
        exception3 = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(users.get(1).getId(), bookingDtoIn));
        assertEquals("Проверь срок аренды", exception3.getMessage());

        bookingDtoIn.setStart(LocalDateTime.now().minusHours(1));
        exception3 = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(users.get(1).getId(), bookingDtoIn));
        assertEquals("Проверь срок аренды", exception3.getMessage());
    }

    @Test
    void giveApprove() {
        Mockito
                .when(bookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(bookings.get(0)));
        Mockito
                .when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(bookings.get(0));

        BookingDtoOut booking1 = bookingService.giveApprove(users.get(0).getId(), bookings.get(0).getId(), true);
        assertEquals(booking1.getStatus(), BookingStatus.APPROVED);
        Mockito
                .when(bookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(bookings.get(1)));
        Mockito
                .when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(bookings.get(1));
        BookingDtoOut booking2 = bookingService.giveApprove(users.get(0).getId(), bookings.get(1).getId(), false);
        assertEquals(booking2.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void giveApproveFail() {
        Mockito
                .when(bookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(bookings.get(0)));

        ObjectNotFoundException exception1 = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.giveApprove(users.get(1).getId(),
                        bookings.get(0).getId(), true));
        assertEquals("Подтверждать может только владелец", exception1.getMessage());

        bookings.get(0).setStatus(BookingStatus.APPROVED);
        ValidationException exception2 = assertThrows(ValidationException.class,
                () -> bookingService.giveApprove(users.get(0).getId(),
                        bookings.get(0).getId(), true));
        assertEquals("Запрос не в стадии запроса подтверждения", exception2.getMessage());

    }

    @Test
    void getBooking() {
        Mockito
                .when(bookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(bookings.get(0)));

        BookingDtoOut bookingDtoOut = bookingService.getBooking(bookings.get(0).getId(), users.get(0).getId());
        assertEquals(bookingDtoOut.getId(), 1);
        assertEquals(bookingDtoOut.getItem().getOwner().getId(), users.get(0).getId());
    }

    @Test
    void getBookingsOfUser() {
        Mockito
                .when(userRepository.existsById(Mockito.anyInt()))
                .thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingsOfUser(users.get(1).getId(),
                        "ALL", 0, 99));
        assertEquals("Пользователь не существует", exception.getMessage());

        Mockito
                .when(bookingRepository.getBookingsByUserId(Mockito.anyInt()))
                .thenReturn(bookings);
        Mockito
                .when(userRepository.existsById(Mockito.anyInt()))
                .thenReturn(true);

        Collection<BookingDtoOut> bookingsAllDtoOut = bookingService.getBookingsOfUser(users.get(1).getId(),
                "ALL", 0, 99);
        assertEquals(bookingsAllDtoOut.size(), 2);
        Collection<BookingDtoOut> bookingsFutureDtoOut = bookingService.getBookingsOfUser(users.get(1).getId(),
                "FUTURE", 0, 99);
        assertEquals(bookingsFutureDtoOut.size(), 2);
        assertTrue(bookingsFutureDtoOut.stream().allMatch(b -> b.getStart().isAfter(LocalDateTime.now())));

        Mockito
                .when(bookingRepository.getBookingsByUserId(Mockito.anyInt()))
                .thenReturn(Collections.emptyList());

        Collection<BookingDtoOut> bookingsPastDtoOut = bookingService.getBookingsOfUser(users.get(1).getId(),
                "PAST", 0, 99);
        assertEquals(bookingsPastDtoOut.size(), 0);
        Collection<BookingDtoOut> bookingsCurrentDtoOut = bookingService.getBookingsOfUser(users.get(1).getId(),
                "CURRENT", 0, 99);
        assertEquals(bookingsCurrentDtoOut.size(), 0);
        Collection<BookingDtoOut> bookingsWaitingDtoOut = bookingService.getBookingsOfUser(users.get(1).getId(),
                "WAITING", 0, 99);
        assertEquals(bookingsPastDtoOut.size(), 0);
        Collection<BookingDtoOut> bookingsRejectedDtoOut = bookingService.getBookingsOfUser(users.get(1).getId(),
                "REJECTED", 0, 99);
        assertEquals(bookingsCurrentDtoOut.size(), 0);
    }

    @Test
    void getBookingsOfOwnerFail() {
        Mockito
                .when(userRepository.existsById(Mockito.anyInt()))
                .thenReturn(false);
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingsOfOwner(users.get(0).getId(), "ALL", 0, 99));
        assertEquals("Пользователь не существует", exception.getMessage());

        Mockito
                .when(userRepository.existsById(Mockito.anyInt()))
                .thenReturn(true);
        UnknownStateException exception2 = assertThrows(UnknownStateException.class,
                () -> bookingService.getBookingsOfOwner(users.get(0).getId(), "MISTAKE", 0, 99));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception2.getMessage());
    }

    @Test
    void getBookingsOfOwner() {
        Mockito
                .when(userRepository.existsById(Mockito.anyInt()))
                .thenReturn(true);
        Mockito
                .when(bookingRepository.findBookingsByItemOwnerId(Mockito.anyInt()))
                .thenReturn(bookings);
        Collection<BookingDtoOut> bookingDtoOuts = bookingService
                .getBookingsOfOwner(users.get(1).getId(), "ALL", 0, 99);
        assertEquals(bookingDtoOuts.size(), 2);
    }

}