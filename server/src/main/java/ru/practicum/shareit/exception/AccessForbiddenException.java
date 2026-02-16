package ru.practicum.shareit.exception;

import lombok.Getter;

/**
 * Исключение, выбрасываемое при попытке доступа к ресурсу, к которому у пользователя нет прав.
 * <p>
 * Возникает в следующих случаях:
 * <ul>
 *   <li>Владелец пытается забронировать свою собственную вещь</li>
 *   <li>Пользователь пытается получить доступ к чужому бронированию</li>
 *   <li>Пользователь пытается изменить или удалить чужую вещь</li>
 *   <li>Пользователь пытается подтвердить бронирование чужой вещи</li>
 * </ul>
 * Обрабатывается глобальным обработчиком исключений и возвращает статус 403 FORBIDDEN.
 * </p>
 *
 * @see ru.practicum.shareit.exception.handler.ExceptionHandlerController
 */
@Getter
public class AccessForbiddenException extends RuntimeException {
    /**
     * Идентификатор пользователя, который пытался выполнить запрещенное действие.
     */
    private final Long userId;

    /**
     * Создает новое исключение с указанным сообщением и идентификатором пользователя.
     *
     * @param message сообщение, описывающее причину исключения
     * @param userId  идентификатор пользователя, вызвавшего исключение
     */
    public AccessForbiddenException(String message, Long userId) {
        super(message);
        this.userId = userId;
    }
}