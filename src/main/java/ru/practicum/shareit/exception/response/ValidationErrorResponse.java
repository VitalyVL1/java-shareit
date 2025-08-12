package ru.practicum.shareit.exception.response;

import java.util.List;

public record ValidationErrorResponse(String message,
                                      List<Violation> violations) {
    public ValidationErrorResponse {
    }
}


