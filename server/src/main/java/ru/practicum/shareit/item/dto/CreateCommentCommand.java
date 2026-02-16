package ru.practicum.shareit.item.dto;

import lombok.Builder;

/**
 * Команда для создания нового комментария в модуле server.
 * <p>
 * Объединяет все необходимые данные для создания комментария в одном объекте:
 * идентификатор вещи, к которой оставляется комментарий, идентификатор автора
 * и DTO с текстом комментария. Используется для передачи данных между слоями
 * приложения и инкапсуляции параметров операции.
 * </p>
 *
 * @param itemId   идентификатор вещи, к которой оставляется комментарий
 * @param authorId идентификатор пользователя-автора комментария
 * @param dto      DTO с текстом комментария
 *
 * @see ru.practicum.shareit.item.dto.CommentCreateOrUpdateDto
 * @see ru.practicum.shareit.item.service.ItemService
 * @see ru.practicum.shareit.item.ItemController
 */
public record CreateCommentCommand(
        Long itemId,
        Long authorId,
        CommentCreateOrUpdateDto dto
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры команды с удобной цепочкой вызовов.
     */
    @Builder
    public CreateCommentCommand {
    }
}