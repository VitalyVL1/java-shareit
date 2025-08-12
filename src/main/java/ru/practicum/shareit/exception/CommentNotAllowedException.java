package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class CommentNotAllowedException extends RuntimeException {
    private final Long userId;
    private final Long itemId;

    public CommentNotAllowedException(String message, Long userId, Long itemId) {
        super(message);
        this.userId = userId;
        this.itemId = itemId;
    }
}
