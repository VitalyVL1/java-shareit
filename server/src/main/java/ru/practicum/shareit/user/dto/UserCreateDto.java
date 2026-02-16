package ru.practicum.shareit.user.dto;

import lombok.Builder;

/**
 * DTO для создания нового пользователя в модуле server.
 * <p>
 * Содержит данные, необходимые для регистрации нового пользователя:
 * имя и электронную почту. Отличается от аналогичного DTO в модуле gateway
 * отсутствием аннотаций валидации, так как валидация уже выполнена на уровне gateway.
 * </p>
 *
 * @param name  имя пользователя
 * @param email электронная почта пользователя
 *
 * @see ru.practicum.shareit.user.model.User
 * @see ru.practicum.shareit.user.UserController
 * @see ru.practicum.shareit.user.service.UserService
 */
public record UserCreateDto(
        String name,
        String email
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов.
     */
    @Builder
    public UserCreateDto {
    }
}