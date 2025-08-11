package ru.practicum.shareit.item.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

public record UpdateItemCommand(
        @Positive Long userId,
        @Positive Long itemId,
        @Valid ItemUpdateDto updateData
) {
    public static UpdateItemCommand of(Long userId, Long itemId, ItemUpdateDto updateData) {
        return new UpdateItemCommand(userId, itemId, updateData);
    }
}
