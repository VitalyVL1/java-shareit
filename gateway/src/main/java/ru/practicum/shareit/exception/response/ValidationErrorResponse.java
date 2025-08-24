package ru.practicum.shareit.exception.response;

import java.util.List;

public record ValidationErrorResponse(String error,
                                      List<Violation> violations) {
}


