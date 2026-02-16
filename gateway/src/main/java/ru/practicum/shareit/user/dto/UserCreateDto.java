package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * DTO для создания нового пользователя в модуле gateway.
 * <p>
 * Содержит данные, необходимые для регистрации нового пользователя:
 * имя и электронную почту. Оба поля обязательны для заполнения,
 * email должен соответствовать стандартному формату.
 * </p>
 *
 * @param name  имя пользователя (не может быть {@code null} или пустым)
 * @param email электронная почта пользователя (не может быть {@code null} или пустой,
 *              должна соответствовать формату email)
 *
 * @see ru.practicum.shareit.user.UserClient
 * @see ru.practicum.shareit.user.UserController
 */
public record UserCreateDto(
        @NotBlank(message = "Имя должно быть указано")
        String name,

        @Email(message = "Некорректный email")
        @NotBlank(message = "Email должен быть указан")
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