package ru.practicum.shareit.item.dto;

import lombok.Builder;

public record ItemCreateDto(
        String name,
        String description,
        Boolean available,
        Long requestId
) {
    @Builder
    public ItemCreateDto {
    }
}
