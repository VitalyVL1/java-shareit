package ru.practicum.shareit.item.dto;

import lombok.Builder;

public record ItemUpdateDto(
        String name,
        String description,
        Boolean available
) {
    @Builder
    public static ItemUpdateDto of(String name, String description, Boolean available) {
        return new ItemUpdateDto(name, description, available);
    }
}
