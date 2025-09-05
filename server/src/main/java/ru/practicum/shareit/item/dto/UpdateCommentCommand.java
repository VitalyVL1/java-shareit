package ru.practicum.shareit.item.dto;

import lombok.Builder;

public record UpdateCommentCommand(
        Long commentId,
        Long itemId,
        Long authorId,
        CommentCreateOrUpdateDto dto
) {
    @Builder
    public UpdateCommentCommand {
    }
}
