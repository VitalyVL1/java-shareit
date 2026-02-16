package ru.practicum.shareit.exception;

import lombok.Getter;

/**
 * Исключение, выбрасываемое при попытке выполнить операцию с недоступной вещью.
 * <p>
 * Возникает в следующих случаях:
 * <ul>
 *   <li>Попытка забронировать вещь, которая помечена как недоступная (available = false)</li>
 *   <li>Попытка забронировать вещь на период, когда она уже забронирована другим пользователем</li>
 * </ul>
 * </p>
 *
 * <p>
 * Обрабатывается глобальным обработчиком исключений и возвращает статус 400 BAD_REQUEST.
 * </p>
 *
 * @see ru.practicum.shareit.exception.handler.ExceptionHandlerController
 * @see ru.practicum.shareit.booking.BookingServiceImpl#save(Long, ru.practicum.shareit.booking.dto.BookingCreateDto)
 */
@Getter
public class UnavailableItemException extends RuntimeException {
    /**
     * Идентификатор вещи, которая недоступна для выполнения операции.
     */
    private final Long itemId;

    /**
     * Создает новое исключение с указанным идентификатором вещи и сообщением.
     *
     * @param itemId  идентификатор вещи, вызвавшей исключение
     * @param message сообщение, описывающее причину недоступности вещи
     */
    public UnavailableItemException(Long itemId, String message) {
        super(message);
        this.itemId = itemId;
    }
}