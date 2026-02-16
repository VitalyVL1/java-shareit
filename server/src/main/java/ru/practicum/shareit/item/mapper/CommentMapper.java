package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentCreateOrUpdateDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

/**
 * Утилитарный класс для преобразования между сущностью {@link Comment} и соответствующими DTO.
 * <p>
 * Содержит статические методы для преобразования объектов комментариев в различные
 * форматы DTO и обратно. Используется в сервисном слое для изоляции логики
 * преобразования и предотвращения циклических зависимостей.
 * </p>
 *
 * @see Comment
 * @see CommentRequestDto
 * @see CommentCreateOrUpdateDto
 */
public class CommentMapper {

    /**
     * Преобразует сущность {@link Comment} в {@link CommentRequestDto}.
     * <p>
     * Извлекает имя автора из связанной сущности {@link User}.
     * </p>
     *
     * @param comment сущность комментария (может быть {@code null})
     * @return DTO с информацией о комментарии или {@code null}, если входной параметр равен {@code null}
     */
    public static CommentRequestDto toCommentRequestDto(Comment comment) {
        if (comment == null) return null;

        return CommentRequestDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    /**
     * Преобразует список сущностей {@link Comment} в список {@link CommentRequestDto}.
     * <p>
     * Применяет {@link #toCommentRequestDto(Comment)} к каждому элементу списка.
     * </p>
     *
     * @param comments список сущностей комментариев (может быть {@code null} или пустым)
     * @return список DTO с информацией о комментариях или пустой список,
     *         если входной параметр равен {@code null} или пуст
     */
    public static List<CommentRequestDto> toCommentRequestDto(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) return Collections.emptyList();

        return comments.stream()
                .map(CommentMapper::toCommentRequestDto)
                .toList();
    }

    /**
     * Создает сущность {@link Comment} из DTO создания и связанных сущностей.
     * <p>
     * Используется при создании нового комментария. Дата создания комментария
     * не устанавливается, так как она будет автоматически заполнена при сохранении
     * (через аннотацию {@link org.springframework.data.annotation.CreatedDate}).
     * </p>
     *
     * @param author пользователь-автор комментария
     * @param item   вещь, к которой оставляется комментарий
     * @param dto    DTO с текстом комментария
     * @return новая сущность комментария или {@code null}, если DTO равен {@code null}
     */
    public static Comment toComment(User author, Item item, CommentCreateOrUpdateDto dto) {
        if (dto == null) return null;

        return Comment.builder()
                .text(dto.text())
                .author(author)
                .item(item)
                .build();
    }
}