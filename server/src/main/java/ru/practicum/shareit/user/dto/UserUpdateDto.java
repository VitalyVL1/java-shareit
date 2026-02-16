package ru.practicum.shareit.user.dto;

import lombok.Builder;

/**
 * DTO для обновления существующего пользователя в модуле server.
 * <p>
 * Содержит поля, которые могут быть обновлены у пользователя. Оба поля являются опциональными,
 * так как клиент может обновить только часть данных. Отсутствие поля или передача {@code null}
 * означает, что значение не изменяется. Отличается от аналогичного DTO в модуле gateway
 * отсутствием аннотаций валидации, так как валидация уже выполнена на уровне gateway.
 * </p>
 *
 * @param name  новое имя пользователя (может быть {@code null}, если не требуется обновление)
 * @param email новая электронная почта пользователя (может быть {@code null}, если не требуется обновление)
 *
 * @see ru.practicum.shareit.user.model.User
 * @see ru.practicum.shareit.user.UserController
 * @see ru.practicum.shareit.user.service.UserService
 */
public record UserUpdateDto(
        String name,
        String email
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO, указывая только те поля, которые необходимо обновить.
     */
    @Builder
    public UserUpdateDto {
    }
}