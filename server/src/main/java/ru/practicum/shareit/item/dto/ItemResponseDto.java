package ru.practicum.shareit.item.dto;

import lombok.Builder;

/**
 * DTO для отображения основной информации о вещи в модуле server.
 * <p>
 * Используется при возврате данных о вещи клиенту в контексте, где не требуется
 * дополнительная информация о бронированиях и комментариях (например, в списке вещей
 * владельца или при поиске). Содержит основные поля вещи и ссылку на запрос,
 * если вещь создана в ответ на запрос.
 * </p>
 *
 * @param id          идентификатор вещи
 * @param name        название вещи
 * @param description описание вещи
 * @param available   флаг доступности вещи для аренды
 * @param requestId   идентификатор запроса, на который отвечает данная вещь (может быть {@code null})
 *
 * @see ru.practicum.shareit.item.model.Item
 * @see ru.practicum.shareit.item.ItemController
 * @see ru.practicum.shareit.item.service.ItemService
 * @see ru.practicum.shareit.request.ItemRequest
 */
public record ItemResponseDto(
        Long id,
        String name,
        String description,
        boolean available,
        Long requestId) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов.
     */
    @Builder
    public ItemResponseDto {
    }
}