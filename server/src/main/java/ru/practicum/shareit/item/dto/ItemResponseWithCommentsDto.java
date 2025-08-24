package ru.practicum.shareit.item.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public record ItemResponseWithCommentsDto(
        Long id,
        String name,
        String description,
        boolean available,
        Long requestId,
        LocalDateTime lastBooking,
        LocalDateTime nextBooking,
        List<CommentRequestDto> comments
) {
    @Builder
    public ItemResponseWithCommentsDto {
    }
}
