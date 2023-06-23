package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemRepository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;


    private final List<Item> items = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        User user1 = new User("user1", "user1@mail.com");
        User user2 = new User("user2", "user2@mail.com");
        User user3 = new User("user3", "user3@mail.com");
        users.add(userRepository.save(user1));
        users.add(userRepository.save(user2));
        users.add(userRepository.save(user3));

        Item item1 = new Item("itemName1", "itemDesc1", false);
        item1.setOwner(user1);
        Item item2 = new Item("itemName2", "itemDesc2", true);
        item2.setOwner(user2);
        Item item3 = new Item("itemName3", "itemDesc3", false);
        item3.setOwner(user3);
        items.add(itemRepository.save(item1));
        items.add(itemRepository.save(item2));
        items.add(itemRepository.save(item3));

        Booking booking1 = new Booking(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        booking1.setId(1);
        booking1.setUser(user1);
        booking1.setItem(item2);
        booking1.setStatus(BookingStatus.APPROVED);
        Booking booking2 = new Booking(LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4));
        booking2.setId(2);
        booking2.setUser(user3);
        booking2.setItem(item2);
        booking2.setStatus(BookingStatus.WAITING);
        Booking booking3 = new Booking(LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(6));
        booking3.setId(3);
        booking3.setUser(user3);
        booking3.setItem(item2);
        booking3.setStatus(BookingStatus.REJECTED);
        bookings.add(bookingRepository.save(booking1));
        bookings.add(bookingRepository.save(booking2));
        bookings.add(bookingRepository.save(booking3));

    }

    @AfterEach
    void afterEach() {
        bookings.clear();
        bookingRepository.deleteAll();
        users.clear();
        userRepository.deleteAll();
        items.clear();
        itemRepository.deleteAll();
    }

    @Test
    void getBookingsByUserId() {
        List<Booking> bookingOfUsers1 = bookingRepository.getBookingsByUserId(users.get(0).getId());
        assertEquals(bookingOfUsers1.size(),1);
        assertEquals(bookingOfUsers1.get(0), bookings.get(0));
        List<Booking> bookingOfUsers2 = bookingRepository.getBookingsByUserId(users.get(1).getId());
        assertTrue(bookingOfUsers2.isEmpty());
        List<Booking> bookingOfUsers3 = bookingRepository.getBookingsByUserId(users.get(2).getId());
        assertEquals(bookingOfUsers3.size(),2);
        assertEquals(bookingOfUsers3.get(0), bookings.get(1));
        assertEquals(bookingOfUsers3.get(1),bookings.get(2));
    }

    @Test
    void findBookingsByItemOwnerId() {
        List<Booking> bookingsOfItemOwner1 = bookingRepository.findBookingsByItemOwnerId(users.get(0).getId());
        assertTrue(bookingsOfItemOwner1.isEmpty());
        List<Booking> bookingsOfItemOwner2 = bookingRepository.findBookingsByItemOwnerId(users.get(1).getId());
        assertEquals(bookingsOfItemOwner2.size(),3);
        assertTrue(bookingsOfItemOwner2.stream()
                .allMatch(booking -> booking.getItem().getOwner().getId() == users.get(1).getId()));
        List<Booking> bookingsOfItemOwner3 = bookingRepository.findBookingsByItemOwnerId(users.get(2).getId());
        assertTrue(bookingsOfItemOwner3.isEmpty());

    }

    @Test
    void findBookingsByItemIdOrderByBookingStart() {
        List<Booking> bookingsByItemId1 = bookingRepository.findBookingsByItemIdOrderByBookingStart(items.get(0).getId());
        assertTrue(bookingsByItemId1.isEmpty());
        List<Booking> bookingsByItemId2 = bookingRepository.findBookingsByItemIdOrderByBookingStart(items.get(1).getId());
        assertEquals(bookingsByItemId2.size(),3);
        assertTrue(bookingsByItemId2.stream().allMatch(booking -> booking.getItem().getId() == items.get(1).getId()));
        assertTrue(bookingsByItemId2.get(0).getBookingStart().isBefore(bookingsByItemId2.get(1).getBookingStart()));
        List<Booking> bookingsByItemId3 = bookingRepository.findBookingsByItemIdOrderByBookingStart(items.get(2).getId());
        assertTrue(bookingsByItemId3.isEmpty());



    }
}