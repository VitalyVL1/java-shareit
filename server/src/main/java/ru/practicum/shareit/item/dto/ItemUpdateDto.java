package ru.practicum.shareit.item.dto;

import lombok.Builder;

public record ItemUpdateDto(
        String name,
        String description,
        Boolean available
) {
    @Builder
    public ItemUpdateDto {
    }
}
