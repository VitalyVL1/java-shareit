package ru.practicum.shareit.item.dto;

import lombok.Builder;

public record ItemResponseDto(
        Long id,
        String name,
        String description,
        boolean available,
        Long requestId) {
    @Builder
    public ItemResponseDto {
    }
}
