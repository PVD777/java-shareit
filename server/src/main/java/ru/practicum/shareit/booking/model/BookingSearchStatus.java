package ru.practicum.shareit.booking.model;

public enum BookingSearchStatus {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static boolean contains(String text) {

        for (BookingSearchStatus t : BookingSearchStatus.values()) {
            if (t.name().equals(text)) {
                return true;
            }
        }
        return false;
    }
}
