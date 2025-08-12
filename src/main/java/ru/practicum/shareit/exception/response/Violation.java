package ru.practicum.shareit.exception.response;

public record Violation(String fieldName,
                        String message,
                        Object rejectedValue) {
    public Violation {
    }
}
