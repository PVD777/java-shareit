package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @Column(name = "booking_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    LocalDateTime bookingStart;
    LocalDateTime bookingFinish;
    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;
    @Enumerated(EnumType.STRING)
    BookingStatus status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    public Booking(LocalDateTime bookingStart, LocalDateTime bookingFinish) {
        this.bookingStart = bookingStart;
        this.bookingFinish = bookingFinish;
    }
}
