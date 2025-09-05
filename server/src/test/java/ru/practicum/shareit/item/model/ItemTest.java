package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    private Item item;
    private Item equalItem1;
    private Item equalItem2;
    private Item notEqualItem;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .build();

        equalItem1 = item.toBuilder().build();
        equalItem2 = item.toBuilder().build();

        notEqualItem = Item.builder()
                .id(2L)
                .build();

    }

    @Test
    void hashCodeTest() {
        assertEquals(item.hashCode(), item.hashCode());
        assertEquals(item.hashCode(), equalItem1.hashCode());
        assertEquals(item.hashCode(), notEqualItem.hashCode());
        assertFalse(item == equalItem1);
    }

    @Test
    void equalsTest() {
        assertEquals(item, equalItem1);
        assertEquals(equalItem1, item);

        assertEquals(item, item);

        assertEquals(item, equalItem1);
        assertEquals(item, equalItem2);
        assertEquals(equalItem1, equalItem2);

        assertNotEquals(item, notEqualItem);
        assertNotEquals(item, null);
        assertNotEquals(item, new Object());
    }
}