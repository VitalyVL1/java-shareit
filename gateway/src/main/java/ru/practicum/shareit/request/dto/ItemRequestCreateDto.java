package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;

public record ItemRequestCreateDto(
        @NotBlank(message = "Описание должно быть указано")
        String description
) {
}
