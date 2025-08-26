package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    private Comment comment;
    private Comment equalComment1;
    private Comment equalComment2;
    private Comment notEqualComment;

    @BeforeEach
    void setUp() {
        comment = Comment.builder()
                .id(1L)
                .build();

        equalComment1 = comment.toBuilder().build();
        equalComment2 = comment.toBuilder().build();

        notEqualComment = Comment.builder()
                .id(2L)
                .build();

    }

    @Test
    void hashCodeTest() {
        assertEquals(comment.hashCode(), comment.hashCode());
        assertEquals(comment.hashCode(), equalComment1.hashCode());
        assertEquals(comment.hashCode(), notEqualComment.hashCode());
        assertFalse(comment == equalComment1);
    }

    @Test
    void equalsTest() {
        assertEquals(comment, equalComment1);
        assertEquals(equalComment1, comment);

        assertEquals(comment, comment);

        assertEquals(comment, equalComment1);
        assertEquals(comment, equalComment2);
        assertEquals(equalComment1, equalComment2);

        assertNotEquals(comment, notEqualComment);
        assertNotEquals(comment, null);
        assertNotEquals(comment, new Object());
    }
}