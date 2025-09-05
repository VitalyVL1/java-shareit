package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class UnavailableItemException extends RuntimeException {
    private final Long itemId;

    public UnavailableItemException(Long itemId, String message) {
        super(message);
        this.itemId = itemId;
    }
}
