package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;

/**
 * DTO для обновления существующего пользователя в модуле gateway.
 * <p>
 * Содержит поля, которые могут быть обновлены у пользователя. Оба поля являются опциональными,
 * так как клиент может обновить только часть данных. Отсутствие поля или передача {@code null}
 * означает, что значение не изменяется. Email при наличии должен соответствовать стандартному формату.
 * </p>
 *
 * @param name  новое имя пользователя (может быть {@code null} или пустым, если не требуется обновление)
 * @param email новая электронная почта пользователя (может быть {@code null} или пустой,
 *              но если указана, должна соответствовать формату email)
 *
 * @see ru.practicum.shareit.user.UserClient
 * @see ru.practicum.shareit.user.UserController
 */
public record UserUpdateDto(
        String name,
        @Email String email
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать экземпляры DTO, указывая только те поля, которые необходимо обновить.
     */
    @Builder
    public UserUpdateDto {
    }
}