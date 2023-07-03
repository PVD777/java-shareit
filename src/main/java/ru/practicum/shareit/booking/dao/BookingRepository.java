package ru.practicum.shareit.booking.dao;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {


    List<Booking> findBookingsByUserId(int userId, Pageable pageable);

    List<Booking> findBookingsByUserIdAndBookingStartAfter(int userId, LocalDateTime date, Pageable pageable);

    List<Booking> findBookingsByUserIdAndBookingFinishBefore(int userId, LocalDateTime date, Pageable pageable);


    @Query(value = "SELECT booking " +
            "FROM Booking booking " +
            "WHERE booking.user.id = :userId AND booking.bookingStart < :date AND booking.bookingFinish > :date")
    List<Booking> findBookingsByUserIdAndBookingStartBeforeAndBookingFinishAfter(@Param("userId") int userId,
                                                                                 @Param("date") LocalDateTime date,
                                                                                 Pageable pageable);

    List<Booking> findBookingsByUserIdAndStatus(int userId, BookingStatus bookingStatus);

    List<Booking> findBookingsByItemOwnerId(int userId, Pageable pageable);

    List<Booking> findBookingsByItemOwnerIdAndBookingStartAfter(int userId, LocalDateTime date, Pageable pageable);

    List<Booking> findBookingsByItemOwnerIdAndBookingFinishBefore(int userId, LocalDateTime date, Pageable pageable);

    @Query(value = "SELECT booking " +
            "FROM Booking booking " +
            "WHERE booking.item.owner.id = :userId AND booking.bookingStart < :date AND booking.bookingFinish > :date")
    List<Booking> findBookingsByItemOwnerIdAndBookingStartBeforeAndBookingFinishAfter(@Param("userId") int userId,
                                                                                      @Param("date") LocalDateTime date,
                                                                                      Pageable pageable);

    List<Booking> findBookingsByItemOwnerIdAndStatus(int userId, BookingStatus bookingStatus);

    List<Booking> findBookingsByItemIdOrderByBookingStart(int userId);

    @Query(value = "SELECT booking " +
            "FROM Booking booking " +
            "WHERE booking.item.id = :itemId AND booking.status = 'APPROVED' AND booking.bookingFinish < :date")
    List<Booking> findBookingsByItemId(@Param("itemId") int itemId, @Param("date") LocalDateTime date);


    @Query(value = "SELECT booking " +
            "FROM Booking booking " +
            "WHERE booking.item.id IN :itemIds AND booking.status = 'APPROVED' ")
    List<Booking> findBookingsByItemIdIn(@Param("itemIds") List<Integer> itemIds);
}
