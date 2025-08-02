package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class DuplicatedDataException extends RuntimeException {
    private final String fieldName;
    private final Object duplicatedValue;

    public DuplicatedDataException(String fieldName, Object duplicatedValue) {
        super(String.format("Field '%s' with value '%s' already exists", fieldName, duplicatedValue));
        this.fieldName = fieldName;
        this.duplicatedValue = duplicatedValue;
    }

    public DuplicatedDataException(String fieldName, Object duplicatedValue, String message) {
        super(message);
        this.fieldName = fieldName;
        this.duplicatedValue = duplicatedValue;
    }
}
