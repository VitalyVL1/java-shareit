package ru.practicum.shareit.item.dto;

import lombok.Builder;

/**
 * DTO для обновления существующей вещи в модуле server.
 * <p>
 * Содержит поля, которые могут быть обновлены у вещи. Все поля являются опциональными,
 * так как клиент может обновить только часть данных. Отсутствие поля или передача {@code null}
 * означает, что значение не изменяется. Отличается от аналогичного DTO в модуле gateway
 * отсутствием аннотаций валидации, так как валидация уже выполнена на уровне gateway.
 * </p>
 *
 * @param name        новое название вещи (может быть {@code null}, если не требуется обновление)
 * @param description новое описание вещи (может быть {@code null}, если не требуется обновление)
 * @param available   новый статус доступности вещи для аренды (может быть {@code null}, если не требуется обновление)
 *
 * @see ru.practicum.shareit.item.model.Item
 * @see ru.practicum.shareit.item.ItemController
 * @see ru.practicum.shareit.item.service.ItemService
 */
public record ItemUpdateDto(
        String name,
        String description,
        Boolean available
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO, указывая только те поля, которые необходимо обновить.
     */
    @Builder
    public ItemUpdateDto {
    }
}