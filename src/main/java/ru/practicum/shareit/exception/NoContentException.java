package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class NoContentException extends RuntimeException {
    private final String contentType;

    public NoContentException(String contentType) {
        super("No content found for content type: " + contentType);
        this.contentType = contentType;
    }
}
