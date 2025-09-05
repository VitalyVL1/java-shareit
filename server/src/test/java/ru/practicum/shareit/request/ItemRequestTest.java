package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    private ItemRequest itemRequest;
    private ItemRequest equalItemRequest1;
    private ItemRequest equalItemRequest2;
    private ItemRequest notEqualItemRequest;

    @BeforeEach
    void setUp() {
        itemRequest = ItemRequest.builder()
                .id(1L)
                .build();

        equalItemRequest1 = itemRequest.toBuilder().build();
        equalItemRequest2 = itemRequest.toBuilder().build();

        notEqualItemRequest = ItemRequest.builder()
                .id(2L)
                .build();

    }

    @Test
    void hashCodeTest() {
        assertEquals(itemRequest.hashCode(), itemRequest.hashCode());
        assertEquals(itemRequest.hashCode(), equalItemRequest1.hashCode());
        assertEquals(itemRequest.hashCode(), notEqualItemRequest.hashCode());
        assertFalse(itemRequest == equalItemRequest1);
    }

    @Test
    void equalsTest() {
        assertEquals(itemRequest, equalItemRequest1);
        assertEquals(equalItemRequest1, itemRequest);

        assertEquals(itemRequest, itemRequest);

        assertEquals(itemRequest, equalItemRequest1);
        assertEquals(itemRequest, equalItemRequest2);
        assertEquals(equalItemRequest1, equalItemRequest2);

        assertNotEquals(itemRequest, notEqualItemRequest);
        assertNotEquals(itemRequest, null);
        assertNotEquals(itemRequest, new Object());
    }
}