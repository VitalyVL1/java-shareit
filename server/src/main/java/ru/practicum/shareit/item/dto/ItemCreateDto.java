package ru.practicum.shareit.item.dto;

import lombok.Builder;

/**
 * DTO для создания новой вещи в модуле server.
 * <p>
 * Содержит данные, необходимые для создания вещи: название, описание,
 * статус доступности и опционально идентификатор запроса (если вещь создается
 * в ответ на запрос другого пользователя). Отличается от аналогичного DTO
 * в модуле gateway отсутствием аннотаций валидации, так как валидация уже
 * выполнена на уровне gateway.
 * </p>
 *
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
public record ItemCreateDto(
        String name,
        String description,
        Boolean available,
        Long requestId
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов.
     */
    @Builder
    public ItemCreateDto {
    }
}