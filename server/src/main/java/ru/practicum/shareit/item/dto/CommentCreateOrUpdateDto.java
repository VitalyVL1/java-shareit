package ru.practicum.shareit.item.dto;

/**
 * DTO для создания или обновления комментария к вещи в модуле server.
 * <p>
 * Содержит текст комментария. Отличается от аналогичного DTO в модуле gateway
 * отсутствием аннотаций валидации, так как валидация уже выполнена на уровне gateway.
 * </p>
 *
 * @param text текст комментария
 *
 * @see ru.practicum.shareit.item.model.Comment
 * @see ru.practicum.shareit.item.ItemController
 * @see ru.practicum.shareit.item.service.ItemService
 */
public record CommentCreateOrUpdateDto(
        String text
) {
}