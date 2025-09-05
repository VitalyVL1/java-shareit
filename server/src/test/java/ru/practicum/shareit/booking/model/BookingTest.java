package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private Booking booking;
    private Booking equalBooking1;
    private Booking equalBooking2;
    private Booking notEqualBooking;

    @BeforeEach
    void setUp() {
        booking = Booking.builder()
                .id(1L)
                .build();

        equalBooking1 = booking.toBuilder().build();
        equalBooking2 = booking.toBuilder().build();

        notEqualBooking = Booking.builder()
                .id(2L)
                .build();

    }

    @Test
    void hashCodeTest() {
        assertEquals(booking.hashCode(), booking.hashCode());
        assertEquals(booking.hashCode(), equalBooking1.hashCode());
        assertEquals(booking.hashCode(), notEqualBooking.hashCode());
        assertFalse(booking == equalBooking1);
    }

    @Test
    void equalsTest() {
        assertEquals(booking, equalBooking1);
        assertEquals(equalBooking1, booking);

        assertEquals(booking, booking);

        assertEquals(booking, equalBooking1);
        assertEquals(booking, equalBooking2);
        assertEquals(equalBooking1, equalBooking2);

        assertNotEquals(booking, notEqualBooking);
        assertNotEquals(booking, null);
        assertNotEquals(booking, new Object());
    }
}