package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;

public record ItemRequestCreateDto(
        @NotNull(message = "Описание должно быть указано")
        String description
) {
}
