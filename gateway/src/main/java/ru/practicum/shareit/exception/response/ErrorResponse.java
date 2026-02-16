package ru.practicum.shareit.exception.response;

/**
 * DTO для стандартизированного ответа с ошибкой в модуле gateway.
 * <p>
 * Используется глобальным обработчиком исключений {@link ru.practicum.shareit.exception.handler.ExceptionHandlerController}
 * для возврата информации об ошибке клиенту в едином формате.
 * Содержит код ошибки и текстовое сообщение.
 * </p>
 *
 * @param code    код ошибки (например, "IllegalArgument", "InternalServer")
 * @param message человекочитаемое описание ошибки
 *
 * @see ru.practicum.shareit.exception.handler.ExceptionHandlerController
 */
public record ErrorResponse(String code,
                            String message) {
}