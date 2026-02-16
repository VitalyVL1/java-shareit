package ru.practicum.shareit.item.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO для отображения информации о комментарии в модуле server.
 * <p>
 * Используется при возврате данных о комментарии клиенту. Содержит полную информацию
 * о комментарии: идентификатор, текст, имя автора и дату создания.
 * </p>
 *
 * @param id         идентификатор комментария
 * @param text       текст комментария
 * @param authorName имя пользователя, оставившего комментарий
 * @param created    дата и время создания комментария
 *
 * @see ru.practicum.shareit.item.model.Comment
 * @see ru.practicum.shareit.item.ItemController
 * @see ru.practicum.shareit.item.service.ItemService
 */
public record CommentRequestDto(
        Long id,
        String text,
        String authorName,
        LocalDateTime created
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов.
     */
    @Builder
    public CommentRequestDto {
    }
}