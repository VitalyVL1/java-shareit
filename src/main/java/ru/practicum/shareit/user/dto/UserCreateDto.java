package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record UserCreateDto(
        @NotBlank(message = "Имя должно быть указано")
        String name,

        @Email(message = "Некорректный email")
        @NotBlank(message = "Email должен быть указан")
        String email
) {
    @Builder
    public static UserCreateDto of(String name, String email) {
        return new UserCreateDto(name, email);
    }
}
