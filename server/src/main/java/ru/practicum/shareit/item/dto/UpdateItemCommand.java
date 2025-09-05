package ru.practicum.shareit.item.dto;

public record UpdateItemCommand(
        Long userId,
        Long itemId,
        ItemUpdateDto updateData
) {
    public static UpdateItemCommand of(Long userId, Long itemId, ItemUpdateDto updateData) {
        return new UpdateItemCommand(userId, itemId, updateData);
    }
}
