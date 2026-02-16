package ru.practicum.shareit.exception;

import lombok.Getter;

/**
 * Исключение, выбрасываемое при попытке доступа к несуществующей сущности.
 * <p>
 * Возникает, когда запрашиваемая сущность (пользователь, вещь, бронирование, запрос)
 * не найдена в базе данных по указанному идентификатору. Содержит информацию о типе
 * сущности и её идентификаторе для формирования информативного сообщения об ошибке.
 * </p>
 *
 * <p>
 * Обрабатывается глобальным обработчиком исключений и возвращает статус 404 NOT_FOUND.
 * </p>
 *
 * @see ru.practicum.shareit.exception.handler.ExceptionHandlerController
 * @see ru.practicum.shareit.user.service.UserServiceImpl
 * @see ru.practicum.shareit.item.service.ItemServiceImpl
 * @see ru.practicum.shareit.booking.BookingServiceImpl
 * @see ru.practicum.shareit.request.service.ItemRequestServiceImpl
 */
@Getter
public class NotFoundException extends RuntimeException {
    /**
     * Название типа сущности, которая не найдена (например, "User", "Item", "Booking", "Request").
     */
    private final String entityName;

    /**
     * Идентификатор сущности, которая не найдена.
     */
    private final Long entityId;

    /**
     * Создает новое исключение с указанным типом сущности и идентификатором.
     * <p>
     * Автоматически формирует сообщение вида:
     * "User with id 123 not found"
     * </p>
     *
     * @param entityName название типа сущности (например, "User")
     * @param id         идентификатор не найденной сущности
     */
    public NotFoundException(String entityName, Long id) {
        super(entityName + " with id " + id + " not found");
        this.entityName = entityName;
        this.entityId = id;
    }
}