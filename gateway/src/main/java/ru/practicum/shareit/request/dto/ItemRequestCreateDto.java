package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO для создания нового запроса вещи в модуле gateway.
 * <p>
 * Используется при отправке запроса на создание запроса вещи, которую пользователь
 * хотел бы взять в аренду. Содержит только описание желаемой вещи.
 * </p>
 *
 * @param description описание вещи, которую пользователь хочет найти
 *                    (не может быть {@code null} или пустым)
 *
 * @see ru.practicum.shareit.request.RequestClient
 * @see ru.practicum.shareit.request.ItemRequestController
 */
public record ItemRequestCreateDto(
        @NotBlank(message = "Описание должно быть указано")
        String description
) {
}