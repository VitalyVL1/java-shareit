package ru.practicum.shareit.request.dto;

/**
 * DTO для создания нового запроса вещи в модуле server.
 * <p>
 * Содержит описание вещи, которую пользователь хотел бы найти.
 * Отличается от аналогичного DTO в модуле gateway отсутствием аннотаций валидации,
 * так как валидация уже выполнена на уровне gateway.
 * </p>
 *
 * @param description описание желаемой вещи
 *
 * @see ru.practicum.shareit.request.ItemRequest
 * @see ru.practicum.shareit.request.ItemRequestController
 * @see ru.practicum.shareit.request.service.ItemRequestService
 */
public record ItemRequestCreateDto(
        String description
) {
}