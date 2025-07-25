package ru.practicum.shareit.exception.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse {
    private final String message;
    private final List<Violation> violations;
}


