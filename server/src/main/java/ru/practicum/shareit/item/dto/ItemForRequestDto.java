package ru.practicum.shareit.item.dto;

import lombok.Builder;

public record ItemForRequestDto(
        Long id,
        String name,
        Long ownerId
) {
    @Builder
    public ItemForRequestDto {
    }
}
