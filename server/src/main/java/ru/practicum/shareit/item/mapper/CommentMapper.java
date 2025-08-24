package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentCreateOrUpdateDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

public class CommentMapper {
    public static CommentRequestDto toCommentRequestDto(Comment comment) {
        if (comment == null) return null;

        return CommentRequestDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentRequestDto> toCommentRequestDto(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) return Collections.emptyList();

        return comments.stream()
                .map(CommentMapper::toCommentRequestDto)
                .toList();
    }

    public static Comment toComment(User author, Item item, CommentCreateOrUpdateDto dto) {
        if (dto == null) return null;

        return Comment.builder()
                .text(dto.text())
                .author(author)
                .item(item)
                .build();
    }
}
