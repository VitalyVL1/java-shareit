package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private User equalUser1;
    private User equalUser2;
    private User notEqualUser;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();

        equalUser1 = user.toBuilder().build();
        equalUser2 = user.toBuilder().build();

        notEqualUser = User.builder()
                .id(2L)
                .build();

    }

    @Test
    void hashCodeTest() {
        assertEquals(user.hashCode(), user.hashCode());
        assertEquals(user.hashCode(), equalUser1.hashCode());
        assertEquals(user.hashCode(), notEqualUser.hashCode());
        assertFalse(user == equalUser1);
    }

    @Test
    void equalsTest() {
        assertEquals(user, equalUser1);
        assertEquals(equalUser1, user);

        assertEquals(user, user);

        assertEquals(user, equalUser1);
        assertEquals(user, equalUser2);
        assertEquals(equalUser1, equalUser2);

        assertNotEquals(user, notEqualUser);
        assertNotEquals(user, null);
        assertNotEquals(user, new Object());
    }
}