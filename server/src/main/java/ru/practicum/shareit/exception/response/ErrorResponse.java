package ru.practicum.shareit.exception.response;

/**
 * DTO для стандартизированного ответа с ошибкой в модуле server.
 * <p>
 * Используется глобальным обработчиком исключений {@link ru.practicum.shareit.exception.handler.ExceptionHandlerController}
 * для возврата информации об ошибке клиенту в едином формате.
 * Содержит код ошибки и текстовое сообщение.
 * </p>
 *
 * @param code    код ошибки (например, "USER_NOT_FOUND", "ACCESS_FORBIDDEN", "ITEM_UNAVAILABLE")
 * @param message человекочитаемое описание ошибки
 *
 * @see ru.practicum.shareit.exception.handler.ExceptionHandlerController
 * @see ru.practicum.shareit.exception.NotFoundException
 * @see ru.practicum.shareit.exception.AccessForbiddenException
 * @see ru.practicum.shareit.exception.UnavailableItemException
 */
public record ErrorResponse(String code,
                            String message) {
}