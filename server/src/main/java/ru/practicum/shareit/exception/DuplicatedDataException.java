package ru.practicum.shareit.exception;

import lombok.Getter;

/**
 * Исключение, выбрасываемое при попытке создания или обновления сущности с данными,
 * которые уже существуют в системе.
 * <p>
 * Возникает, например, при попытке регистрации пользователя с email, который уже
 * используется другим пользователем. Содержит информацию о поле, вызвавшем конфликт,
 * и значении, которое дублируется.
 * </p>
 *
 * <p>
 * Обрабатывается глобальным обработчиком исключений и возвращает статус 409 CONFLICT.
 * </p>
 *
 * @see ru.practicum.shareit.exception.handler.ExceptionHandlerController
 * @see ru.practicum.shareit.user.service.UserServiceImpl#save(ru.practicum.shareit.user.dto.UserCreateDto)
 * @see ru.practicum.shareit.user.service.UserServiceImpl#update(Long, ru.practicum.shareit.user.dto.UserUpdateDto)
 */
@Getter
public class DuplicatedDataException extends RuntimeException {
    /**
     * Название поля, в котором произошло дублирование (например, "email").
     */
    private final String fieldName;

    /**
     * Значение, которое вызвало дублирование (например, "user@example.com").
     */
    private final Object duplicatedValue;

    /**
     * Создает новое исключение с указанным полем и дублирующимся значением.
     * <p>
     * Автоматически формирует сообщение вида:
     * "Field 'email' with value 'user@example.com' already exists"
     * </p>
     *
     * @param fieldName       название поля, вызвавшего дублирование
     * @param duplicatedValue значение, которое дублируется
     */
    public DuplicatedDataException(String fieldName, Object duplicatedValue) {
        super(String.format("Field '%s' with value '%s' already exists", fieldName, duplicatedValue));
        this.fieldName = fieldName;
        this.duplicatedValue = duplicatedValue;
    }
}