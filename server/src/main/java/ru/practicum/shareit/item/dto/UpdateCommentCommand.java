package ru.practicum.shareit.item.dto;

import lombok.Builder;

/**
 * Команда для обновления существующего комментария в модуле server.
 * <p>
 * Объединяет все необходимые данные для обновления комментария в одном объекте:
 * идентификатор комментария, идентификатор вещи, к которой привязан комментарий,
 * идентификатор автора и DTO с новым текстом комментария.
 * Используется для передачи данных между слоями приложения и инкапсуляции параметров операции.
 * </p>
 *
 * @param commentId идентификатор обновляемого комментария
 * @param itemId    идентификатор вещи, к которой привязан комментарий
 * @param authorId  идентификатор автора комментария
 * @param dto       DTO с новым текстом комментария
 *
 * @see ru.practicum.shareit.item.dto.CommentCreateOrUpdateDto
 * @see ru.practicum.shareit.item.service.ItemService
 * @see ru.practicum.shareit.item.ItemController
 */
public record UpdateCommentCommand(
        Long commentId,
        Long itemId,
        Long authorId,
        CommentCreateOrUpdateDto dto
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры команды с удобной цепочкой вызовов.
     */
    @Builder
    public UpdateCommentCommand {
    }
}