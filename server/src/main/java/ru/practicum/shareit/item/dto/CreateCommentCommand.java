package ru.practicum.shareit.item.dto;

import lombok.Builder;

public record CreateCommentCommand(
        Long itemId,
        Long authorId,
        CommentCreateOrUpdateDto dto
) {
    @Builder
    public CreateCommentCommand {
    }
}
