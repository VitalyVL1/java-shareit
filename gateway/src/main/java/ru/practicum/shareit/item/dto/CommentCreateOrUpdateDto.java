package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO для создания или обновления комментария к вещи в модуле gateway.
 * <p>
 * Используется при отправке запроса на добавление нового комментария к вещи
 * после завершения бронирования. Содержит только текст комментария с обязательной валидацией.
 * </p>
 *
 * @param text текст комментария (не может быть {@code null} или пустым)
 */
public record CommentCreateOrUpdateDto(
        @NotBlank(message = "Текст комментария не может быть пустым")
        String text
) {
}