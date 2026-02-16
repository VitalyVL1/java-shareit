package ru.practicum.shareit.item.dto;

import lombok.Builder;

/**
 * DTO для отображения краткой информации о вещи в контексте запроса.
 * <p>
 * Используется при возврате информации о запросе вещи {@link ru.practicum.shareit.request.dto.ItemRequestResponseDto},
 * чтобы показать, какие вещи были предложены в ответ на данный запрос.
 * Содержит только основные идентифицирующие данные вещи.
 * </p>
 *
 * @param id      идентификатор вещи
 * @param name    название вещи
 * @param ownerId идентификатор владельца вещи
 *
 * @see ru.practicum.shareit.request.dto.ItemRequestResponseDto
 * @see ru.practicum.shareit.item.model.Item
 * @see ru.practicum.shareit.item.service.ItemService
 */
public record ItemForRequestDto(
        Long id,
        String name,
        Long ownerId
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов.
     */
    @Builder
    public ItemForRequestDto {
    }
}