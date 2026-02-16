package ru.practicum.shareit.user.dto;

import lombok.Builder;

/**
 * DTO для отображения информации о пользователе в модуле server.
 * <p>
 * Используется при возврате данных о пользователе клиенту. Содержит основную
 * информацию о пользователе: идентификатор, имя и электронную почту.
 * </p>
 *
 * @param id    уникальный идентификатор пользователя
 * @param name  имя пользователя
 * @param email электронная почта пользователя
 *
 * @see ru.practicum.shareit.user.model.User
 * @see ru.practicum.shareit.user.UserController
 * @see ru.practicum.shareit.user.service.UserService
 */
public record UserResponseDto(
        Long id,
        String name,
        String email
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO с удобной цепочкой вызовов.
     */
    @Builder
    public UserResponseDto {
    }
}