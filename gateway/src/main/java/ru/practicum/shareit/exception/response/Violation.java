package ru.practicum.shareit.exception.response;

/**
 * DTO для представления конкретного нарушения валидации в модуле gateway.
 * <p>
 * Используется в составе {@link ValidationErrorResponse} для детализации ошибок валидации.
 * Содержит информацию о поле, которое не прошло валидацию, сообщение об ошибке
 * и значение, вызвавшее нарушение.
 * </p>
 *
 * @param fieldName      имя поля, в котором произошло нарушение
 * @param message        сообщение, описывающее причину нарушения
 * @param rejectedValue  значение, которое было отклонено валидацией (может быть {@code null})
 *
 * @see ValidationErrorResponse
 * @see ru.practicum.shareit.exception.handler.ExceptionHandlerController
 */
public record Violation(String fieldName,
                        String message,
                        Object rejectedValue) {
}