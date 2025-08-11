package ru.practicum.shareit.item.dto;

import lombok.Builder;

public record ItemResponseDto(
        Long id,
        String name,
        String description,
        boolean available,
        Long requestId) {
    @Builder
    public static ItemResponseDto of(Long id, String name, String description, Boolean available, Long requestId) {
        return new ItemResponseDto(id, name, description, available, requestId);
    }
}
