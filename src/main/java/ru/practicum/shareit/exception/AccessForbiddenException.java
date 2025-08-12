package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class AccessForbiddenException extends RuntimeException {
    private final Long userId;

    public AccessForbiddenException(String message, Long userId) {
        super(message);
        this.userId = userId;
    }
}
