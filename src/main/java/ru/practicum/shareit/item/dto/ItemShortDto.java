package ru.practicum.shareit.item.dto;

import lombok.Builder;

public record ItemShortDto(
        Long id,
        String name
) {
    @Builder
    public ItemShortDto {
    }
}
