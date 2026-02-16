package ru.practicum.shareit.exception.response;

import java.util.List;

/**
 * DTO для стандартизированного ответа с детализированными ошибками валидации в модуле gateway.
 * <p>
 * Используется глобальным обработчиком исключений {@link ru.practicum.shareit.exception.handler.ExceptionHandlerController}
 * для возврата подробной информации об ошибках валидации клиенту.
 * Содержит общее сообщение об ошибке и список конкретных нарушений {@link Violation}.
 * </p>
 *
 * @param error      общее сообщение об ошибке валидации (например, "Validation failed")
 * @param violations список нарушений с деталями по каждому полю
 *
 * @see Violation
 * @see ru.practicum.shareit.exception.handler.ExceptionHandlerController
 */
public record ValidationErrorResponse(String error,
                                      List<Violation> violations) {
}