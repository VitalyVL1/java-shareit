package ru.practicum.shareit.exception;

import lombok.Getter;

/**
 * Исключение, выбрасываемое при успешном выполнении запроса, но отсутствии контента для возврата.
 * <p>
 * Возникает, например, когда у пользователя нет бронирований или вещей,
 * и запрос на их получение выполнен успешно, но возвращать нечего.
 * Позволяет вернуть клиенту статус 204 NO_CONTENT вместо пустого списка.
 * </p>
 *
 * <p>
 * Обрабатывается глобальным обработчиком исключений и возвращает статус 204 NO_CONTENT.
 * </p>
 *
 * @see ru.practicum.shareit.exception.handler.ExceptionHandlerController
 * @see ru.practicum.shareit.booking.BookingServiceImpl#findByBookerIdAndState(Long, ru.practicum.shareit.booking.dto.State)
 * @see ru.practicum.shareit.booking.BookingServiceImpl#findByOwnerIdAndState(Long, ru.practicum.shareit.booking.dto.State)
 */
@Getter
public class NoContentException extends RuntimeException {
    /**
     * Тип контента, который отсутствует (например, "Booking", "Item", "Request").
     * Используется для формирования информативного сообщения об ошибке.
     */
    private final String contentType;

    /**
     * Создает новое исключение с указанным типом отсутствующего контента.
     *
     * @param contentType тип контента, который не найден
     */
    public NoContentException(String contentType) {
        super("No content found for content type: " + contentType);
        this.contentType = contentType;
    }
}