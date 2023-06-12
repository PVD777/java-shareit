package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> getBookingsByUserId(int userId);

    List<Booking> findBookingsByItemOwnerId(int userId);

    List<Booking> findBookingsByItemIdOrderByBookingStart(int itemId);
}
