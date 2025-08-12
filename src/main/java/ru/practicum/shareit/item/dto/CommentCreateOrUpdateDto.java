package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateOrUpdateDto(
        @NotBlank(message = "Текст комментария не может быть пустым")
        String text
) {
    public CommentCreateOrUpdateDto {
    }
}
