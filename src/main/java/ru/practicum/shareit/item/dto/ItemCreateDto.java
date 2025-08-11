package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record ItemCreateDto(
        @NotBlank(message = "Название должно быть указано")
        String name,

        @NotBlank(message = "Описание должно быть указано")
        String description,

        @NotNull(message = "Доступность вещи должна быть задана")
        Boolean available,

        Long requestId // если данные будут передаваться в теле
) {
    @Builder
    public static ItemCreateDto of(String name, String description, Boolean available, Long requestId) {
        return new ItemCreateDto(name, description, available, requestId);
    }
}
