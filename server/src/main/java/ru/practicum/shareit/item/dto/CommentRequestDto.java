package ru.practicum.shareit.item.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public record CommentRequestDto(
        Long id,
        String text,
        String authorName,
        LocalDateTime created
) {
    @Builder
    public CommentRequestDto {
    }
}
